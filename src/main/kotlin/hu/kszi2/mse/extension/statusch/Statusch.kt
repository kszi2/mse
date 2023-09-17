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
        var content = ""
        val renderer = SimpleDliRenderer()
        runBlocking {
            val job = launch {
                renderer.renderData(MosogepApiV1(), MosogepApiV2()) { true }
                content = renderer.getData()
            }
        }
        return content
    }

    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "dmoscht") {

                //creating context... mert ugye a szálkezelés egyszerű
                //val content = getData()

                interaction
                    .createFollowupMessageBuilder()
                    .setContent("LAJOS")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(EmbedBuilder())
                    .send()
            }
        }
    }
}

private class StatuschCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("dmoscht", "Request StatuSCH.").createGlobal(api).join()
    }
}