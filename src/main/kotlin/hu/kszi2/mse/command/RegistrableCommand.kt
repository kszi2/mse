package hu.kszi2.mse.command

import org.javacord.api.DiscordApi

interface RegistrableCommand {
    /**
     * Registers the custom command to the api
     *
     * @param api the discord api with a gateway
     */
    fun registerCommand(api: DiscordApi)
}