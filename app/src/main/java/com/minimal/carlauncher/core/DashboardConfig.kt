package com.minimal.carlauncher.core

/** Stable persisted IDs, independent of enum ordinals and translated labels. */
enum class DashboardWidget(val id: String, val minColumns: Int = 1, val minRows: Int = 1) {
    CLOCK("clock"),
    RADIO("radio", 2),
    MAP("map", 2, 2),
    PROJECTION("projection"),
    NAVIGATION("navigation"),
    MUSIC("music");

    companion object {
        fun fromId(id: String): DashboardWidget? = entries.firstOrNull { it.id == id }
    }
}

/** Zero-based logical grid coordinates; a missing widget is hidden. */
data class WidgetPlacement(
    val widget: DashboardWidget,
    val column: Int,
    val row: Int,
    val columns: Int,
    val rows: Int
)

data class DashboardConfig(val placements: List<WidgetPlacement>) {
    fun isValid(): Boolean {
        val occupied = mutableSetOf<Pair<Int, Int>>()
        val seen = mutableSetOf<DashboardWidget>()
        for (p in placements) {
            if (!seen.add(p.widget)) return false
            if (p.columns !in p.widget.minColumns..COLUMNS ||
                p.rows !in p.widget.minRows..ROWS ||
                p.column !in 0..(COLUMNS - p.columns) ||
                p.row !in 0..(ROWS - p.rows)) return false
            for (x in p.column until p.column + p.columns) {
                for (y in p.row until p.row + p.rows) {
                    if (!occupied.add(x to y)) return false
                }
            }
        }
        return true
    }

    companion object {
        const val COLUMNS = 4
        const val ROWS = 2

        // Proposed first modular layout. System controls and app drawer are always
        // outside this grid, so even an empty configuration retains access to settings.
        fun defaults() = DashboardConfig(listOf(
            WidgetPlacement(DashboardWidget.MAP, 0, 0, 2, 2),
            WidgetPlacement(DashboardWidget.CLOCK, 2, 0, 2, 1),
            WidgetPlacement(DashboardWidget.RADIO, 2, 1, 2, 1)
        ))
    }
}
