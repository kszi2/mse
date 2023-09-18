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
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.javacord.api.DiscordApi
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandInteraction

class SchPincer : RegistrableExtension(SchPincerCommand(), SchPincerEvent())

public class SchPincerEvent : RegistrableEvent {

    @Serializable
    data class Opening(
        @SerialName("circleName") val name: String,
        @SerialName("nextOpeningDate") val epoch: Long
    ) {
        override operator fun equals(other: Any?): Boolean {
            if (other is Opening) {
                return this.name == other.name && this.epoch == other.epoch
            }
            return false
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    public suspend fun apiParseBody(): Set<Opening> {
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

    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "opening") {

            }
        }
    }
}

private class SchPincerCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("opening", "Request SCH-PINCÉR openings.").createGlobal(api).join()
    }
}