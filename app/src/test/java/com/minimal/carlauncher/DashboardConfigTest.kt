package com.minimal.carlauncher

import com.minimal.carlauncher.core.DashboardCodec
import com.minimal.carlauncher.core.DashboardConfig
import com.minimal.carlauncher.core.DashboardWidget
import com.minimal.carlauncher.core.WidgetPlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardConfigTest {
    private val defaults = DashboardConfig.defaults()
    private val clock = WidgetPlacement(DashboardWidget.CLOCK, 0, 0, 1, 1)
    private val music = WidgetPlacement(DashboardWidget.MUSIC, 1, 0, 1, 1)

    @Test
    fun `defaults are valid and round trip`() {
        assertTrue(defaults.isValid())
        assertEquals(defaults, DashboardCodec.decode(DashboardCodec.encode(defaults)))
        assertEquals(DashboardWidget.entries.size, DashboardWidget.entries.map { it.id }.distinct().size)
    }

    @Test
    fun `custom positions and hidden widgets round trip`() {
        val custom = DashboardConfig(listOf(music, clock))
        assertTrue(custom.isValid())
        assertEquals(custom, DashboardCodec.decode(DashboardCodec.encode(custom)))
        val empty = DashboardConfig(emptyList())
        assertEquals(empty, DashboardCodec.decode(DashboardCodec.encode(empty)))
    }

    @Test
    fun `invalid dimensions and coordinates are rejected`() {
        listOf(clock.copy(column = -1), clock.copy(row = 2),
            clock.copy(columns = 0), clock.copy(rows = 3),
            clock.copy(column = 3, columns = 2), clock.copy(column = Int.MAX_VALUE),
            clock.copy(columns = Int.MAX_VALUE),
            WidgetPlacement(DashboardWidget.MAP, 0, 0, 1, 1),
            WidgetPlacement(DashboardWidget.RADIO, 0, 0, 1, 1)).forEach {
            assertFalse(DashboardConfig(listOf(it)).isValid())
        }
    }

    @Test
    fun `duplicate widgets and overlaps are rejected`() {
        assertFalse(DashboardConfig(listOf(clock, clock.copy(column = 1))).isValid())
        assertFalse(DashboardConfig(listOf(clock, music.copy(column = 0))).isValid())
        assertEquals(defaults, DashboardCodec.decode(DashboardCodec.encode(DashboardConfig(listOf(clock, clock)))))
    }

    @Test
    fun `bad storage falls back without throwing`() {
        listOf(null, "", "bad json", "{}", """{"version":2,"widgets":[]}""",
            """{"version":1,"widgets":[{"id":"unknown","column":0,"row":0,"columns":1,"rows":1}]}""",
            """{"version":1,"widgets":[{"id":"clock","column":"0","row":0,"columns":1,"rows":1}]}""",
            """{"version":1,"widgets":[{"id":"clock","column":0.5,"row":0,"columns":1,"rows":1}]}""",
            """{"version":1,"widgets":[{"id":"clock","column":-1,"row":0,"columns":1,"rows":1}]}""",
            """{"version":1,"widgets":[{"id":"clock","column":0,"row":0,"columns":1,"rows":1},{"id":"music","column":0,"row":0,"columns":1,"rows":1}]}"""
        ).forEach { assertEquals(defaults, DashboardCodec.decode(it)) }
    }
}
