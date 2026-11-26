package com.minimal.carlauncher

import com.minimal.carlauncher.core.*
import org.junit.Assert.*
import org.junit.Test

class DashboardDraftTest {
    @Test fun hideAndResetPreserveOriginal() {
        val original = DashboardConfig.defaults()
        val draft = DashboardDraft(original)
        draft.hide(DashboardWidget.MAP)
        assertNull(draft.placement(DashboardWidget.MAP))
        assertTrue(draft.snapshot().isValid())
        assertEquals(6, original.placements.size)
        draft.reset()
        assertEquals(original, draft.snapshot())
    }
    @Test fun overlappingDraftCannotBeSaved() {
        val draft = DashboardDraft(DashboardConfig.defaults())
        draft.set(WidgetPlacement(DashboardWidget.CLOCK, 0, 0, 1, 1))
        assertFalse(draft.snapshot().isValid())
    }
    @Test fun findsSpaceAfterHidingWidget() {
        val draft = DashboardDraft(DashboardConfig.defaults())
        assertNull(draft.firstAvailable(DashboardWidget.RADIO)?.takeIf { it.row != 1 })
        draft.hide(DashboardWidget.MAP)
        val p = draft.firstAvailable(DashboardWidget.MAP)
        assertNotNull(p)
        draft.set(p!!)
        assertTrue(draft.snapshot().isValid())
    }
}
