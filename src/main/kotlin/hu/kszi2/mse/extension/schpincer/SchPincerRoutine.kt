package hu.kszi2.mse.extension.schpincer

import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.embed.EmbedBuilder
import java.awt.Color

fun schpincerroutine(api: DiscordApi) {
    val channel = api.getTextChannelById("1147612128103125104")
    if (channel.isEmpty) {
        return
    }

    MessageBuilder().setEmbed(
        EmbedBuilder()
            .setTitle("TESTRUN")
            .setTimestampToNow()
            .addField("testfield", "testvalue")
            .setColor(Color.cyan)
    ).send(channel.get())
}