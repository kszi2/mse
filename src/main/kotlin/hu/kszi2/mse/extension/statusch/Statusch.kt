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

/**
 * Registrable extension that represents the [Statusch] add-on
 * @see RegistrableExtension
 * @see StatuschEvent
 * @see StatuschCommand
 */
class Statusch : RegistrableExtension(StatuschCommand(), StatuschEvent())

private class StatuschEvent : RegistrableEvent {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val apiV1 = MosogepApiV1()
    private val apiV2 = MosogepApiV2()

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun getData(filter: (Machine) -> Boolean): String {
        return try {
            //Instantiate the renderer locally per-request!
            val renderer = SimpleDliRenderer()
            renderer.renderData(apiV1, apiV2) { filter(it) }
            renderer.getData()
        } catch (ex: RuntimeException) {
            "Something went wrong. Try again later."
        }
    }

    private fun figureFilter(option: String): (Machine) -> Boolean {
        return when (option.lowercase()) {
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

    override suspend fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName != "moscht") return@addSlashCommandCreateListener

            // Tell Discord we are thinking (extends timeout window to 15 minutes)
            val responderFuture = interaction.respondLater()

            val expr = interaction.getArgumentStringValueByName("argument")
            val filter = if (expr.isPresent) figureFilter(expr.get()) else { _: Machine -> true }

            // Launch the non-blocking coroutine inside our class-level scope
            scope.launch {
                val data = getData(filter)

                val embed = EmbedBuilder()
                    .setColor(Color.decode("#FFCCEE"))
                    .setTitle("StatuSCH :sweat_drops:")
                    .setDescription(data)
                    .setTimestamp(Instant.now(Clock.systemUTC()))
                    .setUrl("https://mosogep.sch.bme.hu")

                // Edit our original "thinking" response with the final embed
                responderFuture.thenAccept { updater ->
                    updater.addEmbed(embed).update()
                }
            }
        }
    }
}

private class StatuschCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with(
            "moscht",
            "Request StatuSCH.",
            mutableListOf(SlashCommandOption.createStringOption("argument", "a/w/wa/d/da", false))
        )
            .createGlobal(api).join()
    }
}