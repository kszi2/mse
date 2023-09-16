package hu.kszi2.moscht.filter

import hu.kszi2.moscht.Machine
import hu.kszi2.moscht.MachineFilter

sealed class JoinerFilter(
    private val lhs: MachineFilter,
    private val rhs: MachineFilter,
    private val joinOp: (Boolean, Boolean) -> Boolean
) : MachineFilter {

    override fun accept(machine: Machine): Boolean = joinOp(lhs.accept(machine), rhs.accept(machine))
    override fun reportChecked(thing: Any): Boolean = joinOp(lhs.reportChecked(thing), rhs.reportChecked(thing))
}

class ConjunctionFilter(
    lhs: MachineFilter,
    rhs: MachineFilter
) : JoinerFilter(lhs, rhs, { a, b -> a && b }) {
    companion object {
        private fun splitByTwo(filters: Array<out MachineFilter>): MachineFilter {
            return filters.fold(MachineFilter.emptyFilter) { left, acc ->
                ConjunctionFilter(left, acc)
            }
        }
    }

    constructor(starter: MachineFilter, vararg filters: MachineFilter)
            : this(starter, splitByTwo(filters))
}

