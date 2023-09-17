package hu.kszi2.moscht

interface MachineFilter {
    companion object {
        val emptyFilter = object : MachineFilter {
            override fun accept(machine: Machine): Boolean = true
            override fun reportChecked(thing: Any): Boolean = false
        }
    }

    fun accept(machine: Machine): Boolean
    fun reportChecked(thing: Any): Boolean
}
