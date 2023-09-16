package hu.kszi2.moscht

import java.time.Instant
import java.time.temporal.ChronoUnit

class MachineStatus(val status: MachineStatusType, val lastStatus: Instant = Instant.now()) {
    enum class MachineStatusType {
        Available, InUse, Unknown
    }

    fun effectiveStatus(unknownThreshold: Int = 2): Pair<MachineStatusType, Long> {
        val now = Instant.now()
        val diff = ChronoUnit.HOURS.between(lastStatus, now)
        if (diff > unknownThreshold) return Pair(MachineStatusType.Unknown, diff)
        return Pair(status, diff)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MachineStatus

        return effectiveStatus().first == other.effectiveStatus().first
    }

    override fun hashCode(): Int {
        return status.hashCode()
    }
}

sealed class MachineType {
    object WashingMachine : MachineType()
    object Dryer : MachineType()
    data class Unknown(val shortName: String) : MachineType()
}

data class Machine(val level: Int, val type: MachineType, var status: MachineStatus)
