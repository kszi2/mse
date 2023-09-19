package hu.kszi2.kortex

import kotlin.properties.Delegates

class KortexInterval {
    companion object {
        val SECOND = KortexInterval(1000L)

        val MINUTE = KortexInterval(60)

        val HOUR = KortexInterval(1, 0, 0)

        val DAY = KortexInterval(24, 0, 0)
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

    operator fun KortexInterval.timesAssign(other: Int) {
        this.millis = this.delay * other
    }

    operator fun KortexInterval.timesAssign(other: Long) {
        this.millis = this.delay * other
    }

    operator fun KortexInterval.divAssign(other: Int) {
        this.millis = this.delay / other
    }

    operator fun KortexInterval.divAssign(other: Long) {
        this.millis = this.delay / other
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is KortexInterval) {
            return other.millis == this.millis
        }
        return false
    }

    override fun hashCode(): Int {
        return delay.hashCode()
    }
}

operator fun KortexInterval.times(other: Int): KortexInterval {
    return KortexInterval(this.delay * other)
}

operator fun KortexInterval.times(other: Long): KortexInterval {
    return KortexInterval(this.delay * other)
}

operator fun KortexInterval.div(other: Int): KortexInterval {
    return KortexInterval(this.delay / other)
}

operator fun KortexInterval.div(other: Long): KortexInterval {
    return KortexInterval(this.delay / other)
}