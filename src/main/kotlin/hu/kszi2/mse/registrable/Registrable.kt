package hu.kszi2.mse.registrable

import org.javacord.api.DiscordApi

interface RegistrableCommand {
    /**
     * Registers the custom command to the api
     *
     * @param api the discord api with a gateway
     */
    fun registerCommand(api: DiscordApi)
}

interface RegistrableEvent {
    /**
     * Registers the command's event to the api
     *
     * @param api the discord api with a gateway
     */
    fun registerEvent(api: DiscordApi)
}

/**
 * Registers the command to the api
 *
 * @param command the registrable command
 */
private fun DiscordApi.registerCommand(command: RegistrableCommand) {
    command.registerCommand(this@registerCommand)
}

/**
 * Registers the event to the api
 *
 * @param event the registrable event
 */
private fun DiscordApi.registerEvent(event: RegistrableEvent) {
    event.registerEvent(this@registerEvent)
}

/**
 * Registers the extension to the api
 *
 * @param event the registrable event
 */
fun DiscordApi.registerExtension(vararg extensions: RegistrableExtension) {
    extensions.forEach { it.registerExtension(this@registerExtension) }
}


/**
 * Abstract base for extensions
 *
 * @property command the command of the extension
 * @property event the event of the extension
 */
abstract class RegistrableExtension(private val command: RegistrableCommand, private val event: RegistrableEvent) {
    /**
     * Registers the event to the api
     *
     * @param api the discord api with a gateway
     */
    fun registerExtension(api: DiscordApi) {
        api.registerCommand(command)
        api.registerEvent(event)
    }
}