package hu.kszi2.mse.extension.schpincer

import hu.kszi2.mse.registrable.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.component.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.*
import java.awt.Color
import java.time.*

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

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + epoch.hashCode()
            result = 31 * result + negstock.hashCode()
            return result
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun apiParseBody(): Set<Opening> {
        val now: String = apiGetBody(Url("https://schpincer.sch.bme.hu/api/items/now"))
        val tomorrow: String = apiGetBody(Url("https://schpincer.sch.bme.hu/api/items/tomorrow"))

        //parser
        return try {
            val ret = mutableSetOf<Opening>()
            json.decodeFromString<List<Opening>>(now).forEach {
                if (!it.negstock) {
                    ret.add(it)
                }
            }
            json.decodeFromString<List<Opening>>(tomorrow).forEach {
                if (!it.negstock) {
                    ret.add(it)
                }
            }
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

    private fun generateEmbed(openings: Set<Opening>): EmbedBuilder {
        //creating embed base
        val embed = EmbedBuilder()
            .setColor(Color.decode("#db8b12"))
            .setTitle("Openings :fork_and_knife:")
            .setTimestamp(Instant.now(Clock.systemUTC()))
            .setUrl("https://schpincer.sch.bme.hu")

        //if there aren't any openings
        if (openings.isEmpty()) {
            embed.addField("No openings at the moment!", "Try again later.")
            return embed
        }

        embed.addField("Opening name", "", true)
            .addField("Opening time", "", true)
            .addField("Availability", "", true)

        openings.forEach {
            embed.addField("", "**${it.name}**", true)
            embed.addField("", parseDateTime(LocalDateTime.ofEpochSecond(it.epoch / 1000, 0, ZoneOffset.UTC)), true)
            //making it fancy
            val aval = if (it.negstock) {
                ":red_circle:"
            } else {
                ":green_circle:"
            }
            embed.addField("", aval, true)
            embed.addField("", "")
        }
        return embed
    }

    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "opening") {
                //fetch openings
                val openings = runBlocking { apiParseBody() }

                //handling empty openings
                val resp = interaction.createImmediateResponder()
                if (openings.isNotEmpty()) {
                    resp.addComponents(ActionRow.of(Button.link("https://schpincer.sch.bme.hu", "Order!")))
                }

                resp
                    .addEmbed(generateEmbed(openings))
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