package hu.kszi2.mse.extension.schpincer

import hu.kszi2.mse.registrable.RegistrableCommand
import hu.kszi2.mse.registrable.RegistrableEvent
import hu.kszi2.mse.registrable.RegistrableExtension
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.serialization.encodeToString

class SchPincer : RegistrableExtension(SchPincerCommand(), SchPincerEvent())

private class SchPincerEvent : RegistrableEvent {

    @Serializable
    data class Opening(
        @SerialName("circleName") val name: String,
        @SerialName("nextOpeningDate") val epoch: Long,
        @SerialName("outOfStock") val negstock: Boolean
    ) {
        override operator fun equals(other: Any?): Boolean {
            if (other is Opening) {
                return this.name == other.name && this.epoch == other.epoch
            }
            return false
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun apiParseBody(): Set<Opening> {
        val now: String = apiGetBody(Url("https://schpincer.sch.bme.hu/api/items/now"))
        val tomorrow: String = apiGetBody(Url("https://schpincer.sch.bme.hu/api/items/tomorrow"))

        //parser
        return try {
            val ret = mutableSetOf<Opening>()
            ret.addAll(json.decodeFromString<List<Opening>>(now))
            ret.addAll(json.decodeFromString<List<Opening>>(tomorrow))
            ret
        } catch (_: Throwable) {
            setOf()
        }
    }

    private suspend fun apiGetBody(url: Url): String {
        val client = HttpClient(CIO) {
            install(UserAgent) {
                agent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 OPR/102.0.0.0"
            }
        }
        return run { withTimeout(2000) { client.get(url).body() } }
    }

    private fun parseDateTime(datetime: LocalDateTime) =
        "**(${datetime.dayOfWeek})** " +
                "${
                    datetime.dayOfMonth.toString().padStart(2, '0')
                }/${
                    datetime.monthValue.toString().padStart(2, '0')
                }/${datetime.year} ${
                    datetime.hour.toString().padStart(2, '0')
                }:${datetime.minute.toString().padStart(2, '0')}"

    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "opening") {
                //fetch openings
                val openings = runBlocking { apiParseBody() }

                //creating embed
                val embed = EmbedBuilder()
                    .setColor(Color.decode("#FFCCEE"))
                    .setTitle("Openings :fork_and_knife:")
                    .setTimestamp(Instant.now(Clock.systemUTC()))
                    .setUrl("https://schpincer.sch.bme.hu")

                openings.forEach {
                    embed.addField("", it.name, true)
                    embed.addField("", parseDateTime(LocalDateTime.ofEpochSecond(it.epoch / 1000, 0, ZoneOffset.UTC)), true)
                    embed.addField("", "")
                }

                interaction
                    .createImmediateResponder()
                    .setContent("")
                    .addEmbed(embed)
                    .respond()
            }
        }
    }
}

private class SchPincerCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("opening", "Request SCH-PINCÉR openings.").createGlobal(api).join()
    }
}