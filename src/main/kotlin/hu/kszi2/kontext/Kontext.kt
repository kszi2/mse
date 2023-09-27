package hu.kszi2.kontext

import kotlinx.coroutines.*
import kotlinx.datetime.LocalTime
import org.slf4j.LoggerFactory
import kotlin.run

/**
 * A simple [kotlinx.coroutines] based class for running scheduled tasks
 * @property interval the delay on which the task will reoccur
 * @author Kovács Bertalan
 * @since 2023-09-27
 */
@OptIn(DelicateCoroutinesApi::class)
class Kontext {
    var interval: KontextInterval = KontextInterval.SECOND

    private val logger = LoggerFactory.getLogger(Kontext::class.java)

    private fun announceJobRun(interval: KontextInterval, recurring: Boolean = false) {
        if (recurring) {
            logger.debug("Running recurring job with name {} and {} delay", Thread.currentThread().stackTrace[1].methodName, LocalTime.fromMillisecondOfDay(interval.delay.toInt()))
        } else {
            logger.debug("Running one-time job with name ${Thread.currentThread().stackTrace[1].methodName}")
        }
    }

    /**
     * A simple function for running one-time delayed tasks
     * @param execBlock the lambda that the Kortex dispatcher will execute
     * @return the object that the [execBlock] returns
     */
    suspend fun <R> krunOnce(execBlock: suspend () -> R): R {
        val ret = GlobalScope.async {
            delay(this@Kontext.interval.delay)
            announceJobRun(this@Kontext.interval)
            execBlock()
        }.await()
        return ret
    }

    /**
     * A simple function for running reoccurring delayed tasks
     * @param execBlock the lambda that the Kortex dispatcher will execute
     * @return the job that the dispatcher created
     */
    suspend fun <R> krun(execBlock: suspend () -> R): Job {
        val job = GlobalScope.async {
            while (true) {
                announceJobRun(this@Kontext.interval, true)
                execBlock()
                delay(this@Kontext.interval.delay)
            }

        }
        return job
    }
}

/**
 * This function creates a context for a Kontext object
 * and executes the [execFunc] parameter
 * @param execFunc the lambda that the Kontext dispatcher will execute
 * @return the object that the [execFunc] returns
 */
suspend fun <R> kontext(execFunc: suspend Kontext.() -> R): R {
    return run { Kontext().execFunc() }
}