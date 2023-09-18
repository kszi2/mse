package hu.kszi2.mse

import hu.kszi2.mse.extension.ping.Ping
import hu.kszi2.mse.extension.schpincer.SchPincer
import hu.kszi2.mse.extension.schpincer.SchPincerEvent
import hu.kszi2.mse.extension.statusch.Statusch
import hu.kszi2.mse.registrable.registerExtension
import kotlinx.coroutines.runBlocking

/**
 * This is where your bot is...
 */
fun main() {
    SchPincerEvent().getCurrOpening().forEach { println(it.groups["name"]?.value) }

//    bot(BOT_TOKEN) {
//        registerExtension(Statusch(), Ping(), SchPincer())
//        println(createBotInvite().toString())
//    }
}