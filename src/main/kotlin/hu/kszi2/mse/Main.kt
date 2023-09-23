package hu.kszi2.mse

import hu.kszi2.kortex.*
import hu.kszi2.mse.extension.ping.*
import hu.kszi2.mse.extension.schpincer.*
import hu.kszi2.mse.extension.statusch.*
import hu.kszi2.mse.registrable.*

/**
 * Database Path
 */
const val DBPATH = "runtime/data.db"

/**
 * This is where your bot is...
 */
suspend fun main() {
    bot(BOT_TOKEN) {
        registerExtension(Statusch(), Ping(), SchPincer())
        println(createBotInvite().toString())

        kortex {
            interval = KortexInterval.MINUTE * 5
            krunonce { announceNewOpening(this@bot) }
        }
    }
}