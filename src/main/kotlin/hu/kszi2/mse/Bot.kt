package hu.kszi2.mse

import hu.kszi2.mse.command.RegistrableCommand
import hu.kszi2.mse.event.RegistrableEvent
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.intent.Intent

/**
 * Creates a Discord bot via the api
 *
 * @param token the bot token used for authentication
 */

fun bot(token: String, api: DiscordApi.() -> Unit) {
    val apibuild = DiscordApiBuilder()
        .setToken(token)
        .addIntents(Intent.MESSAGE_CONTENT)
        .login().join()
    apibuild.api()
}

/**
 * Registers the command to the api
 *
 * @param commands the registrable commands
 */
private fun DiscordApi.registerCommand(vararg commands: RegistrableCommand) {
    commands.forEach { it.registerCommand(this@registerCommand) }
}

/**
 * Registers the event to the api
 *
 * @param commands the registrable events
 */
private fun DiscordApi.registerEvent(vararg commands: RegistrableEvent) {
    commands.forEach { it.registerEvent(this@registerEvent) }
}


