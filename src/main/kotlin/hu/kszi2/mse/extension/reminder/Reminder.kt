package hu.kszi2.mse.extension.reminder

import hu.kszi2.mse.registrable.RegistrableCommand
import hu.kszi2.mse.registrable.RegistrableEvent
import hu.kszi2.mse.registrable.RegistrableExtension
import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.Message.delete
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption
import java.awt.Color
import java.time.Duration


class Reminder : RegistrableExtension(ReminderCommand(), ReminderEvent())

private class ReminderEvent : RegistrableEvent {
    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "remind") {
                //Getting datetime argument
                val date = interaction.getArgumentStringValueByName("datetime").get()

                //Try parsing datetime.
                val datetime = try {
                    parse(date)
                } catch (e: Exception) {
                    interaction.createImmediateResponder()
                        .setContent("**Invalid datetime syntax. (Use yyyy-MM-dd HH:mm)**")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond()
                    //exit lambda
                    return@addSlashCommandCreateListener
                }
                //Getting other arguments
                val title = interaction.getArgumentStringValueByName("title").get()
                val location = try {
                    interaction.getArgumentStringValueByName("location").get()
                } catch (_: Exception) {
                    ""
                } //maybe null
                val description = try {
                    interaction.getArgumentStringValueByName("description").get()
                } catch (_: Exception) {
                    ""
                } //maybe null

                //Create example notification
                interaction.createImmediateResponder()
                    .setContent("Is this correct?")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(
                        EmbedBuilder()
                            .setColor(Color.decode("#FFCCEE"))
                            .setTitle(title + "\t\t\t" + location)
                            .setDescription(description)
                            .setFooter(
                                "${
                                    datetime?.dayOfMonth.toString().padStart(2, '0')
                                }/${
                                    datetime?.monthValue.toString().padStart(2, '0')
                                }/${datetime?.year} ${
                                    datetime?.hour.toString().padStart(2, '0')
                                }:${datetime?.minute.toString().padStart(2, '0')}"
                            )
                    )
                    .addComponents(
                        ActionRow.of(
                            Button.success("simpleremindsucc", "Yes"),
                            Button.danger("simpleremindfail", "No"),
                        )
                    )
                    .respond()
            }
        }

        //Register interaction event

        api.addMessageComponentCreateListener { event ->
            val messageComponentInteraction = event.messageComponentInteraction
            val customId = messageComponentInteraction.customId
            when (customId) {
                "simpleremindsucc" -> messageComponentInteraction.let {

                    //TODO: database

                    it.createOriginalMessageUpdater().removeAllComponents().update()
                    it.createFollowupMessageBuilder()
                        .setContent("**Added** reminder!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .send()
                }

                "simpleremindfail" -> messageComponentInteraction.let {

                    it.createOriginalMessageUpdater().removeAllComponents().update()
                    it.createFollowupMessageBuilder()
                        .setContent("**Removed** reminder!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .send()
                }
            }
        }
    }
}

private class ReminderCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with(
            "remind", "Create a simple reminder.", mutableListOf(
                SlashCommandOption.createStringOption("title", "Title", true),
                SlashCommandOption.createStringOption("datetime", "Datetime", true),
                SlashCommandOption.createStringOption("location", "Location", false),
                SlashCommandOption.createStringOption("description", "Description", false),
            )
        ).createGlobal(api).join()
    }
}