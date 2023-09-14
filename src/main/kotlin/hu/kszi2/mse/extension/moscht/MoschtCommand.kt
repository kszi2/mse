package hu.kszi2.mse.extension.moscht

import hu.kszi2.mse.command.RegistrableCommand
import org.javacord.api.DiscordApi
import org.javacord.api.interaction.SlashCommand

class MoschtCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("moscht", "Request StatuSCH.").createGlobal(api).join()
    }
}