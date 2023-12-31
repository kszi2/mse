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
 * @param invokedApi the block code that the [DiscordApi] invokes
 */
suspend fun bot(token: String, invokedApi: suspend DiscordApi.() -> Unit) {
    val api = DiscordApiBuilder()
        .setToken(token)
        .addIntents(Intent.MESSAGE_CONTENT)
        .login().join()

    api.invokedApi()
}

