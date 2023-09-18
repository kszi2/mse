package hu.kszi2.kabal

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.run

class Kabal(var interval: KabalInterval = KabalInterval.SECOND) {
    suspend fun <R> krunonce(block: () -> R): R {
        return run {
            delay(this.interval.delay)
            block.invoke()
        }
    }


    suspend fun krun(block: () -> Unit) {
        run {
            while (true) {
                block.invoke()
                delay(this.interval.delay)
            }
        }

    }
}