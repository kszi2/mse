package hu.kszi2.moscht.filter

import hu.kszi2.moscht.Machine
import hu.kszi2.moscht.MachineFilter
import hu.kszi2.moscht.MachineStatus

class StatusFilter(private val stat: MachineStatus) : MachineFilter {
    override fun accept(machine: Machine): Boolean = machine.status == stat
    override fun reportChecked(thing: Any): Boolean {
        return thing == stat.status
    }
}
