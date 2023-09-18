package hu.kszi2.mse

import hu.kszi2.mse.extension.ping.*
import hu.kszi2.mse.extension.schpincer.*
import hu.kszi2.mse.extension.statusch.*
import hu.kszi2.mse.registrable.*

/**
 * This is where your bot is...
 */
fun main() {
    bot(BOT_TOKEN) {
        registerExtension(Statusch(), Ping(), SchPincer())
        println(createBotInvite().toString())
    }
}