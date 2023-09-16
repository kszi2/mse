package hu.kszi2.moscht.parsing

import hu.kszi2.moscht.Machine
import hu.kszi2.moscht.MachineStatus
import hu.kszi2.moscht.MachineStatus.MachineStatusType
import hu.kszi2.moscht.MachineType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Instant

class DefaultJsonParser(val unknownThreshold: Int = 3) : MachineParser {
    @Serializable
    private data class LevelV1(
        val id: Int,
        val machines: List<InternalMachineV1>,
        @SerialName("last_query_time") val lastQueryTime: String,
    )

    @Serializable
    private data class InternalMachineV1(
        val id: Int,
        @SerialName("kind_of") val type: String,
        val status: Int,
        val message: String?,
    )

    @Serializable
    private data class ResponseObjectV2(
        val floors: List<LevelV2>,
    )

    @Serializable
    private data class LevelV2(
        val id: Int,
        val machines: List<InternalMachineV2>,
    )

    @Serializable
    private data class InternalMachineV2(
        val id: Int,
        @SerialName("kindOf") val type: String,
        val status: String,
        val lastQueryTime: String,
        val lastChanged: String,
    )

    override fun parse(payload: String): List<Machine> {
        return try {
            val internals = Json.decodeFromString<List<LevelV1>>(payload)
            internals.flatMap { lvl ->
                lvl.machines.map { machine ->
                    Machine(lvl.id, parseTypeShortString(machine.type), parseStatus(machine.status, lvl.lastQueryTime))
                }
            }
        } catch (ex: SerializationException) { // could not parse as v1 response, try v2
            val internals = Json.decodeFromString<ResponseObjectV2>(payload)
            internals.floors
                .flatMap { lvl ->
                    lvl.machines
                        .map { machine ->
                            Machine(
                                lvl.id,
                                parseTypeShortString(machine.type),
                                parseStatus(machine.status, machine.lastQueryTime)
                            )
                        }
                }
                .sortedBy(Machine::level)
        }
    }

    private fun parseTypeShortString(shortType: String): MachineType {
        return when (shortType) {
            "WM", "Washer" -> MachineType.WashingMachine
            "DR", "Dryer" -> MachineType.Dryer
            else -> MachineType.Unknown(shortType)
        }
    }

    private fun parseStatus(returnedStatus: Int, queryTime: String): MachineStatus {
        val statType = when (returnedStatus) {
            0 -> MachineStatusType.Available
            1 -> MachineStatusType.InUse
            else -> MachineStatusType.Unknown
        }
        return MachineStatus(statType, Instant.parse(queryTime))
    }

    private fun parseStatus(returnedStatus: String, queryTime: String): MachineStatus {
        val statType = when (returnedStatus) {
            "Available" -> MachineStatusType.Available
            "NotAvailable" -> MachineStatusType.InUse
            else -> MachineStatusType.Unknown
        }
        return MachineStatus(statType, Instant.parse(queryTime))
    }
}
