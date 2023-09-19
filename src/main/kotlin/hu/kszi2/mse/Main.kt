package hu.kszi2.mse

import hu.kszi2.mse.extension.ping.Ping
import hu.kszi2.mse.extension.schpincer.SchPincer
import hu.kszi2.mse.extension.statusch.Statusch
import hu.kszi2.mse.registrable.registerExtension

/**
 * This is where your bot is...
 */
suspend fun main() {
    bot(BOT_TOKEN) {
        registerExtension(Statusch(), Ping(), SchPincer())
        println(createBotInvite().toString())
    }
}