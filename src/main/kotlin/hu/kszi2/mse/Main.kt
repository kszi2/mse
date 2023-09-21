package hu.kszi2.mse

import hu.kszi2.kortex.*
import hu.kszi2.mse.database.*
import hu.kszi2.mse.database.dbTransaction
import hu.kszi2.mse.extension.ping.Ping
import hu.kszi2.mse.extension.schpincer.*
import hu.kszi2.mse.extension.statusch.Statusch
import hu.kszi2.mse.registrable.registerExtension

/**
 * Database Path
 */
const val DBPATH = "runtime/data.db"

/**
 * This is where your bot is...
 */
suspend fun main() {
    dbInitialize()
    bot(BOT_TOKEN) {
        registerExtension(Statusch(), Ping(), SchPincer())
        println(createBotInvite().toString())

        kortex {
            interval = KortexInterval.SECOND * 5
            krunonce { schpincerJob(this@bot) }
        }
    }
}