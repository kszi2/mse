package hu.kszi2.mse.extension.statusch

import hu.kszi2.mse.registrable.*
import org.javacord.api.*
import org.javacord.api.interaction.*
import hu.kszi2.moscht.rendering.SimpleDliRenderer

class Statusch : RegistrableExtension(StatuschCommand(), StatuschEvent())

private class StatuschEvent : RegistrableEvent {
    override fun registerEvent(api: DiscordApi) {
        TODO("Not yet implemented")
    }
}

private class StatuschCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("hu/kszi2/moscht", "Request StatuSCH.").createGlobal(api).join()
    }
}