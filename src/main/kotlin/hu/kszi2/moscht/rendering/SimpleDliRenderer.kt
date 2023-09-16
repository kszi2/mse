package hu.kszi2.moscht.rendering

import hu.kszi2.moscht.Machine
import hu.kszi2.moscht.MachineStatus
import hu.kszi2.moscht.MachineType
import hu.kszi2.moscht.MosogepAsyncApi

class SimpleDliRenderer : MachineRenderer {

    private var reply = ""
    override suspend fun renderData(vararg apis: MosogepAsyncApi, filter: (m: Machine) -> Boolean) {
        val ex = RuntimeException("Cannot reach any API")
        reply = ""
        for (api in apis) {
            try {
                return api.loadMachines().filter(filter).forEach(::renderMachine)
            } catch (timeout: MosogepAsyncApi.UnreachableApiError) {
                ex.addSuppressed(timeout)
            }
        }
        throw ex
    }

    fun getData(): String {
        return reply
    }

    private fun renderMachine(machine: Machine) {
        reply += if (machine.level < 10) {
            "`0${machine.level}.` ${renderType(machine.type)}: ${renderStatus(machine.status)}\n"
        } else {
            "`${machine.level}.` ${renderType(machine.type)}: ${renderStatus(machine.status)}\n"
        }
    }

    private fun renderStatus(status: MachineStatus): String {
        val (stat, since) = status.effectiveStatus()
        return when (stat) {
            MachineStatus.MachineStatusType.Available -> " :green_circle:"
            MachineStatus.MachineStatusType.InUse -> " :red_circle:"
            MachineStatus.MachineStatusType.Unknown -> " :no_entry: (${since} órája)"
        }
    }

    private fun renderType(type: MachineType): String {
        return when (type) {
            is MachineType.WashingMachine -> "Mosógép   "
            is MachineType.Dryer -> "Szárítógép"
            is MachineType.Unknown -> "Ismeretlen"
        }
    }
}