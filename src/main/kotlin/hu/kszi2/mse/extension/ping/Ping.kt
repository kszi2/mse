package hu.kszi2.mse.extension.ping

import hu.kszi2.mse.registrable.*
import org.javacord.api.*
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.*

class Ping : RegistrableExtension(PingCommand(), PingEvent())

private class PingEvent : RegistrableEvent {
    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "ping") {
                interaction
                    .createImmediateResponder()
                    .setContent("pong")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond()
            }
        }
    }
}

private class PingCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("ping", "Replies with pong.").createGlobal(api).join()
    }
}