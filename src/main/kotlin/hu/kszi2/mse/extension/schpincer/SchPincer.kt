package hu.kszi2.mse.extension.schpincer

import hu.kszi2.mse.database.DBOpening
import hu.kszi2.mse.database.DBOpenings
import hu.kszi2.mse.database.dbTransaction
import hu.kszi2.mse.registrable.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.*
import org.jetbrains.exposed.sql.selectAll
import java.awt.Color
import java.time.*

class SchPincer : RegistrableExtension(SchPincerCommand(), SchPincerEvent())

internal class SchPincerEvent : RegistrableEvent {

    @Serializable
    data class Opening(
        @SerialName("circleName") var name: String,
        @SerialName("nextOpeningDate") var epoch: Long,
        @SerialName("outOfStock") var negstock: Boolean
    ) {
        override operator fun equals(other: Any?): Boolean {
            if (other is Opening) {
                return this.name == other.name && this.epoch == other.epoch
            }
            return false
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + epoch.hashCode()
            result = 31 * result + negstock.hashCode()
            return result
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    internal suspend fun apiParseBody(): Set<Opening> {
        val now: String = apiGetBody(Url("https://schpincer.sch.bme.hu/api/items/now"))
        val tomorrow: String = apiGetBody(Url("https://schpincer.sch.bme.hu/api/items/tomorrow"))

        //parser
        return try {
            val ret = mutableSetOf<Opening>()
            json.decodeFromString<List<Opening>>(now).forEach {
                if (!it.negstock) {
                    ret.add(it)
                }
            }
            json.decodeFromString<List<Opening>>(tomorrow).forEach {
                if (!it.negstock) {
                    ret.add(it)
                }
            }
            ret
        } catch (_: Throwable) {
            setOf()
        }
    }

    private suspend fun apiGetBody(url: Url): String {
        val client = HttpClient(CIO) {
            install(UserAgent) {
                agent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 OPR/102.0.0.0"
            }
        }
        return run { withTimeout(2000) { client.get(url).body() } }
    }

    private fun parseDateTime(datetime: LocalDateTime) =
        "**(${datetime.dayOfWeek})** " +
                "${
                    datetime.dayOfMonth.toString().padStart(2, '0')
                }/${
                    datetime.monthValue.toString().padStart(2, '0')
                }/${datetime.year} ${
                    datetime.hour.toString().padStart(2, '0')
                }:${datetime.minute.toString().padStart(2, '0')}"

    internal fun generateEmbed(openings: Set<Opening>): EmbedBuilder {
        //creating embed base
        val embed = EmbedBuilder()
            .setColor(Color.decode("#db8b12"))
            .setTitle("Openings :fork_and_knife:")
            .setTimestamp(Instant.now(Clock.systemUTC()))
            .setUrl("https://schpincer.sch.bme.hu")

        //if there aren't any openings
        if (openings.isEmpty()) {
            embed.addField("No openings at the moment!", "Try again later.")
            return embed
        }

        openings.forEach {
            embed.addField("**${it.name}** :green_circle:", parseDateTime(LocalDateTime.ofEpochSecond(it.epoch / 1000, 0, ZoneOffset.ofHours(2))), true)
            embed.addField("", "")
        }
        return embed
    }

    override fun registerEvent(api: DiscordApi) {
        api.addSlashCommandCreateListener { event ->
            val interaction: SlashCommandInteraction = event.slashCommandInteraction
            if (interaction.fullCommandName == "opening") {
                //clean out old openings
                cleanOpeningsFromDB()

                //fetch openings
                val openings = readOpeningsFromDB()

                //handling empty openings
                val resp = interaction.createImmediateResponder()
                if (openings.isNotEmpty()) {
                    resp.addComponents(ActionRow.of(Button.link("https://schpincer.sch.bme.hu", "Order!")))
                }

                resp
                    .addEmbed(generateEmbed(openings.toSet()))
                    .respond()
            }
        }
    }
}

private class SchPincerCommand : RegistrableCommand {
    override fun registerCommand(api: DiscordApi) {
        SlashCommand.with("opening", "Request SCH-PINCÉR openings.").createGlobal(api).join()
    }
}

/**
 * Database related stuff
 */

private fun getNewOpeningsViaAPI(): List<SchPincerEvent.Opening> {
    val event = SchPincerEvent()
    val apiOpeningContent = runBlocking { event.apiParseBody().toMutableList() }

    if (apiOpeningContent.isEmpty()) {
        return listOf() //empty list
    }

    val databaseOpeningContent = readOpeningsFromDB()

    apiOpeningContent.removeAll(databaseOpeningContent)

    if (apiOpeningContent.isEmpty()) {
        return listOf()
    }

    return apiOpeningContent
}

private fun readOpeningsFromDB(): MutableList<SchPincerEvent.Opening> {
    var databaseOpeningContent = mutableListOf<SchPincerEvent.Opening>()

    dbTransaction {
        databaseOpeningContent = DBOpenings.selectAll().map {
            SchPincerEvent.Opening(
                it[DBOpenings.circleName],
                it[DBOpenings.nextOpeningDate],
                it[DBOpenings.outOfStock]
            )
        }.toMutableList()
    }

    return databaseOpeningContent
}

private fun moveNewOpeningsToDB(): List<SchPincerEvent.Opening> {
    val newOpenings = getNewOpeningsViaAPI()

    if (newOpenings.isNotEmpty()) {
        dbTransaction {
            newOpenings.forEach {
                DBOpening.new {
                    circleName = it.name
                    nextOpeningDate = it.epoch
                    outOfStock = it.negstock
                }
            }
        }
    }

    return newOpenings
}

private fun cleanOpeningsFromDB() {
    dbTransaction {
        val oldOpenings = DBOpening.find {
            DBOpenings.nextOpeningDate lessEq LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(0))
        }
        oldOpenings.forEach { it.delete() }
    }
}

fun announceNewOpening(api: DiscordApi): Unit {
    val channel = api.getTextChannelById("1147612128103125104")

    if (channel.isEmpty) {
        return
    }

    val newOpenings = moveNewOpeningsToDB()

    if (newOpenings.isNotEmpty()) {
        val event = SchPincerEvent()
        //handling empty openings
        val resp = MessageBuilder()
        resp.addComponents(ActionRow.of(Button.link("https://schpincer.sch.bme.hu", "Order!")))

        resp.setEmbed(event.generateEmbed(newOpenings.toSet())).send(channel.get())
    }
}