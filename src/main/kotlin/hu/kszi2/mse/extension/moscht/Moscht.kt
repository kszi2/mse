package hu.kszi2.mse.extension.moscht

import hu.kszi2.mse.registrable.*
import org.javacord.api.*
import org.javacord.api.interaction.*

class Moscht : RegistrableExtension(MoschtCommand(), MoschtEvent())

private class MoschtEvent : RegistrableEvent {
    override fun registerEvent(api: DiscordApi) {
        TODO("Not yet implemented")
    }
}

private class MoschtCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("moscht", "Request StatuSCH.").createGlobal(api).join()
    }
}