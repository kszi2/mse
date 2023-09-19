package hu.kszi2.mse

import org.javacord.api.*
import org.javacord.api.entity.intent.Intent
import java.io.File

/**
 * Reads int the BOT token located in the runtime library under the name bot-token.txt
 *
 * @throws RuntimeException if the file could not be found
 */
internal val BOT_TOKEN = try {
    File("runtime/bot-token.txt").bufferedReader().use { it.readText() }.trim()
} catch (error: Exception) {
    throw RuntimeException(
        "Failed to load bot token. Message: ", error
    )
}

/**
 * Creates a Discord bot via the api
 *
 * @param token the bot token used for authentication
 * @return the constructed DiscordApi
 */

suspend fun bot(token: String, ignoredApi: suspend DiscordApi.() -> Unit) {
    val apibuild = DiscordApiBuilder()
        .setToken(token)
        .addIntents(Intent.MESSAGE_CONTENT)
        .login().join()

    apibuild.ignoredApi()
}

