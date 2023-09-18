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
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.javacord.api.DiscordApi
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption

class SchPincer : RegistrableExtension(SchPincerCommand(), SchPincerEvent())

public class SchPincerEvent : RegistrableEvent {

    public fun getCurrOpening(): Sequence<MatchResult> {
        var body: String?
        runBlocking {
            withTimeout(2000) {
                body = apiGetBody(Url("https://schpincer.sch.bme.hu/api/items/now"))
            }
        }

        if (body == null) {
            throw NullPointerException("The request body was empty!")
        }

        //filter
        //src: @bodand
        return Regex("\"circleName\":\"(?<name>.+?)\"").findAll(body!!)
    }

    @OptIn(InternalAPI::class)
    private suspend fun apiGetBody(url: Url): String {
        val client = HttpClient(CIO) {
            install(UserAgent) {
                agent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 OPR/102.0.0.0"
            }
        }

        return client.get(url).body()
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