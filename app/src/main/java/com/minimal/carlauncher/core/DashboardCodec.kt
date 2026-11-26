package com.minimal.carlauncher.core

import org.json.JSONArray
import org.json.JSONObject

/** Versioned, fail-safe storage. Never trust malformed preferences during launcher start. */
object DashboardCodec {
    private const val VERSION = 1

    fun encode(config: DashboardConfig): String {
        val safe = if (config.isValid()) config else DashboardConfig.defaults()
        val widgets = JSONArray()
        safe.placements.forEach { p ->
            widgets.put(JSONObject()
                .put("id", p.widget.id)
                .put("column", p.column)
                .put("row", p.row)
                .put("columns", p.columns)
                .put("rows", p.rows))
        }
        return JSONObject().put("version", VERSION).put("widgets", widgets).toString()
    }

    fun decode(raw: String?): DashboardConfig {
        if (raw.isNullOrBlank()) return DashboardConfig.defaults()
        return try {
            val obj = JSONObject(raw)
            if (obj.get("version") != VERSION) return DashboardConfig.defaults()
            val widgets = obj.getJSONArray("widgets")
            if (widgets.length() > DashboardWidget.entries.size) return DashboardConfig.defaults()
            val placements = List(widgets.length()) { i ->
                val item = widgets.getJSONObject(i)
                val widget = DashboardWidget.fromId(item.get("id") as String)
                    ?: return DashboardConfig.defaults()
                WidgetPlacement(widget, item.get("column") as Int, item.get("row") as Int,
                    item.get("columns") as Int, item.get("rows") as Int)
            }
            DashboardConfig(placements).takeIf { it.isValid() } ?: DashboardConfig.defaults()
        } catch (_: Exception) {
            DashboardConfig.defaults()
        }
    }
}
