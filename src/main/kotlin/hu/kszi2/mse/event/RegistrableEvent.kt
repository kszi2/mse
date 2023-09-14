package hu.kszi2.mse.event

import org.javacord.api.DiscordApi

interface RegistrableEvent {
    /**
     * Registers the command's event to the api
     *
     * @param api the discord api with a gateway
     */
    fun registerEvent(api: DiscordApi)
}