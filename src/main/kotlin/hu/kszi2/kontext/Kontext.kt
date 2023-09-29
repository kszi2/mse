package hu.kszi2.kontext

import kotlinx.coroutines.*
import kotlin.properties.Delegates
import kotlinx.datetime.LocalTime
import org.slf4j.LoggerFactory
import kotlin.run

/**
 * A simple [kotlinx.coroutines] based library for running scheduled tasks
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
 * Interval class for the [Kontext] library
 * @property delay the amount of delay in milliseconds
 * @property SECOND static ONE second interval
 * @property MINUTE static ONE minute interval
 * @property HOUR static ONE hour interval
 * @property DAY static ONE day interval
 * @constructor create an interval for the [Kontext] object
 */
class KontextInterval {
    companion object {
        val SECOND = KontextInterval(1000L)

        val MINUTE = KontextInterval(60)

        val HOUR = KontextInterval(1, 0, 0)

        val DAY = KontextInterval(24, 0, 0)
    }

    private var millis by Delegates.notNull<Long>()

    val delay: Long
        get() {
            return millis
        }

    constructor(milliseconds: Long) {
        millis = milliseconds
    }

    constructor(seconds: Int) : this((seconds * 1000).toLong())

    constructor(minutes: Int, seconds: Int) {
        require(minutes in 0..60) { "Minutes must be between 0 and 60" }
        require(seconds in 0..60) { "Seconds must be between 0 and 60" }
        millis = (seconds * 1_000 + minutes * 60_000).toLong()
    }

    constructor(hours: Int, minutes: Int, seconds: Int) : this(minutes, seconds) {
        require(hours in 0..24) { "Hours must be between 0 and 24" }
        millis += (hours * 3_600_000).toLong()
    }

    operator fun KontextInterval.timesAssign(other: Int) {
        this.millis = this.delay * other
    }

    operator fun KontextInterval.timesAssign(other: Long) {
        this.millis = this.delay * other
    }

    operator fun KontextInterval.divAssign(other: Int) {
        this.millis = this.delay / other
    }

    operator fun KontextInterval.divAssign(other: Long) {
        this.millis = this.delay / other
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is KontextInterval) {
            return other.millis == this.millis
        }
        return false
    }

    override fun hashCode(): Int {
        return delay.hashCode()
    }
}

operator fun KontextInterval.times(other: Int): KontextInterval {
    return KontextInterval(this.delay * other)
}

operator fun KontextInterval.times(other: Long): KontextInterval {
    return KontextInterval(this.delay * other)
}

operator fun KontextInterval.div(other: Int): KontextInterval {
    return KontextInterval(this.delay / other)
}

operator fun KontextInterval.div(other: Long): KontextInterval {
    return KontextInterval(this.delay / other)
}

/**
 * This function creates a context for a Kontext object
 * and executes the [execFunc] parameter
 * @param execFunc the lambda that the [Kontext] dispatcher will execute
 * @return the object that the [execFunc] returns
 */
suspend fun <R> kontext(execFunc: suspend Kontext.() -> R): R {
    return run { Kontext().execFunc() }
}

/**
 * Registers a reoccurring job with a given interval
 * @param interval the interval the job should repeat itself
 * @param execFunc the lambda that the [Kontext] dispatcher will execute
 */
suspend fun <R> registerJob(interval: KontextInterval, execFunc: suspend () -> R) {
    kontext {
        this.interval = interval
        krun {
            execFunc.invoke()
        }
    }
}