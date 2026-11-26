package com.minimal.carlauncher.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.minimal.carlauncher.R
import com.minimal.carlauncher.core.DashboardConfig
import com.minimal.carlauncher.core.DashboardDraft
import com.minimal.carlauncher.core.DashboardWidget
import com.minimal.carlauncher.core.WidgetPlacement

object DashboardEditorDialog {
    fun show(activity: Activity, initial: DashboardConfig, onSave: (DashboardConfig) -> Unit) {
        val draft = DashboardDraft(initial)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_dashboard_editor, null, false)
        val widgetSpinner = view.findViewById<Spinner>(R.id.editorWidget)
        val visible = view.findViewById<CheckBox>(R.id.editorVisible)
        val column = view.findViewById<Spinner>(R.id.editorColumn)
        val row = view.findViewById<Spinner>(R.id.editorRow)
        val width = view.findViewById<Spinner>(R.id.editorWidth)
        val height = view.findViewById<Spinner>(R.id.editorHeight)
        val status = view.findViewById<TextView>(R.id.editorStatus)
        val preview = view.findViewById<TextView>(R.id.editorPreview)
        val widgets = DashboardWidget.entries
        val names = listOf(R.string.editor_clock, R.string.editor_radio, R.string.editor_map,
            R.string.editor_projection, R.string.editor_navigation, R.string.editor_music)
        fun adapter(items: List<String>) = ArrayAdapter(activity, android.R.layout.simple_spinner_item, items)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        widgetSpinner.adapter = adapter(names.map { activity.getString(it) })
        column.adapter = adapter((1..DashboardConfig.COLUMNS).map { it.toString() })
        row.adapter = adapter((1..DashboardConfig.ROWS).map { it.toString() })
        width.adapter = adapter((1..DashboardConfig.COLUMNS).map { it.toString() })
        height.adapter = adapter((1..DashboardConfig.ROWS).map { it.toString() })

        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.editor_title)
            .setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.editor_save, null)
            .create()
        var loading = false
        var selected = widgets.first()
        fun refreshPreview() {
            val config = draft.snapshot()
            val cells = Array(DashboardConfig.ROWS) { Array(DashboardConfig.COLUMNS) { mutableListOf<Int>() } }
            config.placements.forEach { p ->
                for (y in p.row until (p.row + p.rows).coerceAtMost(DashboardConfig.ROWS))
                    for (x in p.column until (p.column + p.columns).coerceAtMost(DashboardConfig.COLUMNS))
                        if (x >= 0 && y >= 0) cells[y][x].add(widgets.indexOf(p.widget) + 1)
            }
            preview.text = cells.joinToString("\n") { line ->
                line.joinToString("   ") { cell ->
                    when (cell.size) { 0 -> "·"; 1 -> cell.first().toString(); else -> "!" }
                }
            } + "\n" + names.mapIndexed { i, name -> "${i + 1} ${activity.getString(name)}" }.joinToString(" · ")
            val valid = config.isValid()
            status.setText(if (valid) R.string.editor_valid else R.string.editor_invalid)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = valid
        }
        fun loadSelected() {
            loading = true
            val p = draft.placement(selected)
            visible.isChecked = p != null
            column.setSelection(p?.column ?: 0)
            row.setSelection(p?.row ?: 0)
            width.setSelection((p?.columns ?: selected.minColumns) - 1)
            height.setSelection((p?.rows ?: selected.minRows) - 1)
            listOf(column, row, width, height).forEach { it.isEnabled = p != null }
            loading = false
            refreshPreview()
        }
        fun update() {
            if (loading || !visible.isChecked) return
            draft.set(WidgetPlacement(selected, column.selectedItemPosition, row.selectedItemPosition,
                width.selectedItemPosition + 1, height.selectedItemPosition + 1))
            refreshPreview()
        }
        // Explicit Apply button avoids asynchronous Spinner selection callbacks changing another widget.
        view.findViewById<Button>(R.id.editorApply).setOnClickListener { update() }
        widgetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, item: View?, position: Int, id: Long) {
                selected = widgets[position]
                loadSelected()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        visible.setOnCheckedChangeListener { _, checked ->
            if (!loading) {
                if (checked) draft.set(draft.firstAvailable(selected)
                    ?: WidgetPlacement(selected, 0, 0, selected.minColumns, selected.minRows))
                else draft.hide(selected)
                loadSelected()
            }
        }
        view.findViewById<Button>(R.id.editorReset).setOnClickListener {
            draft.reset()
            loadSelected()
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            update()
            val config = draft.snapshot()
            if (config.isValid()) {
                onSave(config)
                dialog.dismiss()
            }
        }
        loadSelected()
    }
}
