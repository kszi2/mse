package hu.kszi2.mse

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
