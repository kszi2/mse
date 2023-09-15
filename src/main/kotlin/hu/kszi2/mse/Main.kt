package hu.kszi2.mse

import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandInteraction

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