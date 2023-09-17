package hu.kszi2.mse.extension.statusch

import hu.kszi2.moscht.*
import hu.kszi2.mse.registrable.*
import org.javacord.api.*
import org.javacord.api.interaction.*
import hu.kszi2.moscht.rendering.*
import kotlinx.coroutines.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import java.awt.Color
import java.time.Clock
import java.time.Instant

class Statusch : RegistrableExtension(StatuschCommand(), StatuschEvent())

private class StatuschEvent : RegistrableEvent {
    private fun getData(filter: (Machine) -> Boolean): String {
        var content = "Something went wrong. Try again later."
        val renderer = SimpleDliRenderer()
        //creating context... mert ugye a szálkezelés egyszerű
        runBlocking {
            val job = launch {
                renderer.renderData(MosogepApiV1(), MosogepApiV2()) { filter(it) }
                content = renderer.getData()
            }
            job.join()
        }
        return content
    }

    private fun figureFilter(option: String): (Machine) -> Boolean {
        return when (option) {
            "w" -> { m: Machine -> m.type == MachineType.WashingMachine }

            "wa" -> { m: Machine ->
                m.type == MachineType.WashingMachine && m.status == MachineStatus(MachineStatus.MachineStatusType.Available)
            }

            "d" -> { m: Machine -> m.type == MachineType.Dryer }

            "da" -> { m: Machine ->
                m.type == MachineType.Dryer && m.status == MachineStatus(MachineStatus.MachineStatusType.Available)
            }

            else -> { _: Machine -> true }
        }
    }

    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "moscht") {
                val expr = interaction.getArgumentStringValueByName("argument")

                val filter = if (expr.isPresent) {
                    figureFilter(expr.get())
                } else { m: Machine -> true }

                val embed = EmbedBuilder()
                    .setColor(Color.decode("#FFCCEE"))
                    .setTitle("StatuSCH :sweat_drops:")
                    .setDescription(getData(filter))
                    .setTimestamp(Instant.now(Clock.systemUTC()))
                    .setUrl("https://mosogep.sch.bme.hu")

                interaction
                    .createImmediateResponder()
                    .setContent("")
                    .addEmbed(embed)
                    .respond()
            }
        }
    }
}

private class StatuschCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("moscht", "Request StatuSCH.", mutableListOf(SlashCommandOption.createStringOption("argument", "a/w/wa/d/da", false)))
            .createGlobal(api).join()
    }
}