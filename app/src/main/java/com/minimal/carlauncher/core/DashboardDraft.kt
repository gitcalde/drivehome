package com.minimal.carlauncher.core

/** A private editable copy. Invalid intermediate layouts are allowed but never saved. */
class DashboardDraft(initial: DashboardConfig) {
    private val placements = initial.placements.associateBy { it.widget }.toMutableMap()
    fun placement(widget: DashboardWidget): WidgetPlacement? = placements[widget]
    fun set(placement: WidgetPlacement) { placements[placement.widget] = placement }
    fun hide(widget: DashboardWidget) { placements.remove(widget) }
    fun reset() {
        placements.clear()
        DashboardConfig.defaults().placements.forEach(::set)
    }
    fun snapshot() = DashboardConfig(placements.values.toList())
    fun firstAvailable(widget: DashboardWidget): WidgetPlacement? {
        for (row in 0..DashboardConfig.ROWS - widget.minRows)
            for (column in 0..DashboardConfig.COLUMNS - widget.minColumns) {
                val candidate = WidgetPlacement(widget, column, row, widget.minColumns, widget.minRows)
                if (DashboardConfig(placements.values.filter { it.widget != widget } + candidate).isValid())
                    return candidate
            }
        return null
    }
}
