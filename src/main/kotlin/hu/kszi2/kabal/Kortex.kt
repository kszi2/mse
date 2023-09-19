package hu.kszi2.kabal

import kotlinx.coroutines.*
import kotlin.run

@OptIn(DelicateCoroutinesApi::class)
class Kortex {
    private var scope: GlobalScope = GlobalScope
    var interval: KortexInterval = KortexInterval.SECOND
        get() {
            return field
        }
        set(value) {
            field = value
        }

    companion object {
        val DEFAULT = Kortex()
    }

    internal suspend fun <R> krunonce(block: () -> R): R {
        val ret = scope.async {
            delay(this@Kortex.interval.delay)
            block()
        }.await()
        return ret
    }

    suspend fun <R> krun(block: () -> R): Job {
        val job = scope.async {
            while (true) {
                block()
                delay(this@Kortex.interval.delay)
            }

        }
        return job
    }
}

suspend fun <R> kortex(func: suspend Kortex.() -> R): R {
    return run { Kortex.DEFAULT.func() }
}