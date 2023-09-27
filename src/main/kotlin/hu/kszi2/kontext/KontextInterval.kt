package hu.kszi2.kontext

import kotlin.properties.Delegates

class KontextInterval {
    companion object {
        val SECOND = KontextInterval(1000L)

        val MINUTE = KontextInterval(60)

        val HOUR = KontextInterval(1, 0, 0)

        val DAY = KontextInterval(24, 0, 0)
    }

    private var millis by Delegates.notNull<Long>()

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

    val delay: Long
        get() {
            return millis
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