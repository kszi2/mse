package hu.kszi2.moscht.parsing

import hu.kszi2.moscht.Machine

interface MachineAsyncParser {
    suspend fun parse(payload: String): List<Machine>
}

interface MachineParser {
    fun parse(payload: String): List<Machine>
}
