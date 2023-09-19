package hu.kszi2.kortex

import kotlinx.coroutines.*
import kotlin.run

@OptIn(DelicateCoroutinesApi::class)
class Kortex {
    private var scope: GlobalScope = GlobalScope
    var interval: KortexInterval = KortexInterval.SECOND

    companion object {
        val DEFAULT = Kortex()
    }

    suspend fun <R> krunonce(ignoredBlock: () -> R): R {
        val ret = scope.async {
            delay(this@Kortex.interval.delay)
            ignoredBlock()
        }.await()
        return ret
    }

    suspend fun <R> krun(ignoredBlock: () -> R): Job {
        val job = scope.async {
            while (true) {
                ignoredBlock()
                delay(this@Kortex.interval.delay)
            }

        }
        return job
    }
}

suspend fun <R> kortex(ignoredFunc: suspend Kortex.() -> R): R {
    return run { Kortex.DEFAULT.ignoredFunc() }
}