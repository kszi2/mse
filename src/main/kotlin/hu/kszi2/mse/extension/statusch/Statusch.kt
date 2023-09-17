package hu.kszi2.mse.extension.statusch

import hu.kszi2.moscht.*
import hu.kszi2.mse.registrable.*
import org.javacord.api.*
import org.javacord.api.interaction.*
import hu.kszi2.moscht.rendering.*
import kotlinx.coroutines.*
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.embed.EmbedBuilder

class Statusch : RegistrableExtension(StatuschCommand(), StatuschEvent())

private class StatuschEvent : RegistrableEvent {
    private fun getData(): String {
        var content = "Something went wrong. Try again later."
        val renderer = SimpleDliRenderer()
        //creating context... mert ugye a szálkezelés egyszerű
        runBlocking {
            val job = launch {
                renderer.renderData(MosogepApiV1(), MosogepApiV2()) { true }
                content = renderer.getData()
            }
            job.join()
        }
        return content
    }

    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "dmoscht") {
                val content = getData()
                interaction
                    .createImmediateResponder()
                    .setContent(content)
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond()

            }
        }
    }
}

private class StatuschCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("dmoscht", "Request StatuSCH.").createGlobal(api).join()
    }
}