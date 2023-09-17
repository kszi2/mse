package hu.kszi2.mse.extension.schpincer

import hu.kszi2.mse.registrable.RegistrableCommand
import hu.kszi2.mse.registrable.RegistrableEvent
import hu.kszi2.mse.registrable.RegistrableExtension
import org.javacord.api.DiscordApi

class SchPincer : RegistrableExtension(SchPincerCommand(), SchPincerEvent())

private class SchPincerEvent : RegistrableEvent {
    override fun registerEvent(api: DiscordApi) {
        TODO("Not yet implemented")
    }
}

private class SchPincerCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        TODO("Not yet implemented")
    }
}