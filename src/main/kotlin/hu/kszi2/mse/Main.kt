package hu.kszi2.mse

import hu.kszi2.mse.extension.ping.Ping
import hu.kszi2.mse.registrable.registerExtension

/**
 * This is where your bot is...
 */
fun main() {
    bot(BOT_TOKEN) {
        registerExtension(Ping())
    }
}