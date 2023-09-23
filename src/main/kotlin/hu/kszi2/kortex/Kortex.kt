package hu.kszi2.kortex

import kotlinx.coroutines.*
import kotlin.run

@OptIn(DelicateCoroutinesApi::class)
class Kortex {
    var interval: KortexInterval = KortexInterval.SECOND

    suspend fun <R> krunonce(ignoredBlock: suspend () -> R): R {
        val ret = GlobalScope.async {
            delay(this@Kortex.interval.delay)
            ignoredBlock()
        }.await()
        return ret
    }

    suspend fun <R> krun(ignoredBlock: suspend () -> R): Job {
        val job = GlobalScope.async {
            while (true) {
                ignoredBlock()
                delay(this@Kortex.interval.delay)
            }

        }
        return job
    }
}

suspend fun <R> kortex(ignoredFunc: suspend Kortex.() -> R): R {
    return run { Kortex().ignoredFunc() }
}