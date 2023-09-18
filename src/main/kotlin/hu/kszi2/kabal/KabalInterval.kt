package hu.kszi2.kabal

import kotlin.properties.Delegates

class KabalInterval {
    companion object {
        val SECOND = KabalInterval(1000L)

        val MINUTE = KabalInterval(60)

        val HOUR = KabalInterval(1, 0, 0)

        val DAY = KabalInterval(24, 0, 0)
    }

    private var millis by Delegates.notNull<Long>()

    constructor(milliseconds: Long) {
        millis = milliseconds
    }

    constructor(seconds: Int) : this((seconds * 1000).toLong()) {
    }

    constructor(minutes: Int, seconds: Int) {
        require(minutes in 0..60) { "Minutes must be between 0 and 60" }
        require(seconds in 0..60) { "Seconds must be between 0 and 60" }
        millis = (seconds * 1_000 + minutes * 60_000).toLong()
    }

    constructor(hours: Int, minutes: Int, seconds: Int) : this(minutes, seconds) {
        require(hours in 0..24) { "Hours must be between 0 and 24" }
        millis += (hours * 3_600_000).toLong()
    }

    val delay: Long
        get() {
            return millis
        }

    operator fun KabalInterval.timesAssign(other: Int) {
        this.millis = this.delay * other
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is KabalInterval) {
            return other.millis == this.millis
        }
        return false
    }

    override fun hashCode(): Int {
        return delay.hashCode()
    }
}

operator fun KabalInterval.times(other: Int): KabalInterval {
    return KabalInterval(this.delay * other)
}

operator fun KabalInterval.div(other: Int): KabalInterval {
    return KabalInterval(this.delay / other)
}

operator fun KabalInterval.div(other: Long): KabalInterval {
    return KabalInterval(this.delay / other)
}