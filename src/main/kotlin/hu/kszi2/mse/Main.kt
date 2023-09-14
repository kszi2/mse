package hu.kszi2.mse

import org.javacord.api.event.message.MessageCreateEvent
import java.io.*


private val BOT_TOKEN = try {
    File("runtime/bot-token.txt").bufferedReader().use { it.readText() }.trim()
} catch (error: Exception) {
    throw RuntimeException(
        "Failed to load bot token. Message: ", error
    )
}

fun main() {
    bot(BOT_TOKEN) {

        //ping-listener
        addMessageCreateListener { event: MessageCreateEvent ->
            if (event.messageContent.equals("!ping", ignoreCase = true)) {
                event.channel.sendMessage("Pong!")
            }
        }
    }
}