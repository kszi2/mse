package hu.kszi2.mse

import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandInteraction
import java.io.*


/**
 * Reads int the BOT token located in the runtime library under the name bot-token.txt
 *
 * @throws RuntimeException if the file could not be found
 */
private val BOT_TOKEN = try {
    File("runtime/bot-token.txt").bufferedReader().use { it.readText() }.trim()
} catch (error: Exception) {
    throw RuntimeException(
        "Failed to load bot token. Message: ", error
    )
}


fun main() {
    bot(BOT_TOKEN) {

        //ping-command
        SlashCommand.with("ping", "Checks the functionality of this command")
            .createGlobal(this@bot)
            .join()
        //ping-command-listener
        addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "ping") {
                interaction
                    .createImmediateResponder()
                    .setContent("pong")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            }
        }
    }
}