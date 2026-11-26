package com.minimal.carlauncher.ui

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.minimal.carlauncher.R
import com.minimal.carlauncher.core.DashboardConfig
import com.minimal.carlauncher.core.DashboardWidget
import com.minimal.carlauncher.databinding.ActivityHomeBinding

/** Positions existing views without recreating widgets or moving their controllers. */
class DashboardController(binding: ActivityHomeBinding) {
    private val grid = binding.dashboardGrid
    private val widgets = mapOf(
        DashboardWidget.MAP to binding.mapPortal.root,
        DashboardWidget.CLOCK to binding.gaugeClock.root,
        DashboardWidget.RADIO to binding.radioCard.root,
        DashboardWidget.PROJECTION to binding.cardProjection.root,
        DashboardWidget.NAVIGATION to binding.cardNav.root,
        DashboardWidget.MUSIC to binding.cardMusic.root
    )
    private val columns = IntArray(DashboardConfig.COLUMNS + 1) { index ->
        if (index == 0 || index == DashboardConfig.COLUMNS) ConstraintSet.PARENT_ID
        else View.generateViewId()
    }
    private val rows = IntArray(DashboardConfig.ROWS + 1) { index ->
        if (index == 0 || index == DashboardConfig.ROWS) ConstraintSet.PARENT_ID
        else View.generateViewId()
    }

    fun apply(config: DashboardConfig) {
        val safe = config.takeIf { it.isValid() } ?: DashboardConfig.defaults()
        val halfGap = grid.resources.getDimensionPixelSize(R.dimen.gap) / 2
        val set = ConstraintSet()
        set.clone(grid)
        for (i in 1 until DashboardConfig.COLUMNS) {
            set.create(columns[i], ConstraintSet.VERTICAL_GUIDELINE)
            set.setGuidelinePercent(columns[i], i.toFloat() / DashboardConfig.COLUMNS)
        }
        for (i in 1 until DashboardConfig.ROWS) {
            set.create(rows[i], ConstraintSet.HORIZONTAL_GUIDELINE)
            set.setGuidelinePercent(rows[i], i.toFloat() / DashboardConfig.ROWS)
        }
        widgets.forEach { (widget, view) ->
            val id = view.id
            set.clear(id)
            set.constrainWidth(id, 0)
            set.constrainHeight(id, 0)
            val p = safe.placements.firstOrNull { it.widget == widget }
            if (p == null) {
                set.setVisibility(id, View.GONE)
            } else {
                val end = p.column + p.columns
                val bottom = p.row + p.rows
                set.setVisibility(id, View.VISIBLE)
                set.connect(id, ConstraintSet.LEFT, columns[p.column],
                    ConstraintSet.LEFT, if (p.column == 0) 0 else halfGap)
                set.connect(id, ConstraintSet.RIGHT, columns[end],
                    if (end == DashboardConfig.COLUMNS) ConstraintSet.RIGHT else ConstraintSet.LEFT,
                    if (end == DashboardConfig.COLUMNS) 0 else halfGap)
                set.connect(id, ConstraintSet.TOP, rows[p.row],
                    ConstraintSet.TOP, if (p.row == 0) 0 else halfGap)
                set.connect(id, ConstraintSet.BOTTOM, rows[bottom],
                    if (bottom == DashboardConfig.ROWS) ConstraintSet.BOTTOM else ConstraintSet.TOP,
                    if (bottom == DashboardConfig.ROWS) 0 else halfGap)
            }
        }
        set.applyTo(grid)
    }
}
