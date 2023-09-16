package hu.kszi2.moscht

class MachineStore(private val machines: List<Machine> = listOf()) {
    suspend fun reloadFromApi(api: MosogepAsyncApi) = MachineStore(api.loadMachines())

    fun listMachines(filter: MachineFilter): List<Machine> {
        if (filter === MachineFilter.emptyFilter) return machines
        return machines.filter(filter::accept)
    }

    fun isEmpty(): Boolean {
        return machines.isEmpty()
    }
}
