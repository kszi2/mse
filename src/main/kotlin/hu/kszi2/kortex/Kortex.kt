package hu.kszi2.kortex

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.run

@OptIn(DelicateCoroutinesApi::class)
class Kortex {
    var interval: KortexInterval = KortexInterval.SECOND

    private val logger = LoggerFactory.getLogger(Kortex::class.java)

    private fun announceJobRun(interval: KortexInterval, recurring: Boolean = false) {
        if (recurring) {
            logger.debug("Running recurring job with name: ${Thread.currentThread().stackTrace[1].methodName} and ${interval.delay} ms delay")
        } else {
            logger.debug("Running one-time job with name: ${Thread.currentThread().stackTrace[1].methodName}")
        }
    }

    suspend fun <R> krunonce(ignoredBlock: suspend () -> R): R {
        val ret = GlobalScope.async {
            delay(this@Kortex.interval.delay)
            announceJobRun(this@Kortex.interval)
            ignoredBlock()
        }.await()
        return ret
    }

    suspend fun <R> krun(ignoredBlock: suspend () -> R): Job {
        val job = GlobalScope.async {
            while (true) {
                announceJobRun(this@Kortex.interval, true)
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