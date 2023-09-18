package hu.kszi2.kabal

import kotlinx.coroutines.CoroutineDispatcher

data class KabalBuilder(
    var interval: KabalInterval = KabalInterval.MINUTE,
) {
    fun createKabal(): Kabal {
        return Kabal(interval)
    }
}