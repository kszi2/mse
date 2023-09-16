package hu.kszi2.moscht.filter

import hu.kszi2.moscht.Machine
import hu.kszi2.moscht.MachineFilter
import hu.kszi2.moscht.MachineType

class TypeFilter(private val type: MachineType) : MachineFilter {
    override fun accept(machine: Machine): Boolean =
        machine.type == type

    override fun reportChecked(thing: Any): Boolean =
        thing == type
}