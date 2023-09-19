package hu.kszi2.kabal

import kotlinx.coroutines.*
import kotlin.run

@OptIn(DelicateCoroutinesApi::class)
class Kabal {
    private var scope: GlobalScope = GlobalScope
    var interval: KabalInterval = KabalInterval.SECOND
        get() {
            return field
        }
        set(value) {
            field = value
        }

    companion object {
        val DEFAULT = Kabal()
    }

    internal suspend fun <R> krunonce(block: () -> R): R {
        val ret = scope.async {
            delay(this@Kabal.interval.delay)
            block()
        }.await()
        return ret
    }

    suspend fun <R> krun(block: () -> R): Job {
        val job = scope.async {
            while (true) {
                block()
                delay(this@Kabal.interval.delay)
            }

        }
        return job
    }
}

suspend fun <R> kabal(func: suspend Kabal.() -> R): R {
    return run { Kabal.DEFAULT.func() }
}