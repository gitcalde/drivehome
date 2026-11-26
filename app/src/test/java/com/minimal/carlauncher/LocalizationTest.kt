package com.minimal.carlauncher

import com.minimal.carlauncher.core.Format
import com.minimal.carlauncher.nav.LatLon
import com.minimal.carlauncher.nav.Maneuvers
import com.minimal.carlauncher.nav.RouteStep
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class LocalizationTest {
    private fun step(type: String, modifier: String? = null, road: String = "", exit: Int? = null) =
        RouteStep(type, modifier, road, LatLon(43.0, -4.0), exit)

    @Test
    fun `navigation respects English and Spanish and preserves road names`() {
        val turn = step("turn", "left", "A-8")
        assertEquals("Turn left onto A-8", Maneuvers.instruction(turn, "en"))
        assertEquals("Gira a la izquierda hacia A-8", Maneuvers.instruction(turn, "es"))
        assertEquals("Gira a la izquierda hacia A-8", Maneuvers.instruction(turn, "fr"))
        assertEquals("↶", Maneuvers.glyph(step("turn", "uturn")))
    }

    @Test
    fun `roundabout exits and arrival are localized`() {
        val roundabout = step("roundabout", exit = 2)
        assertEquals("At the roundabout, take the 2nd exit", Maneuvers.instruction(roundabout, "en"))
        assertEquals("En la rotonda, toma la salida 2", Maneuvers.instruction(roundabout, "es"))
        assertEquals("Arrive at destination", Maneuvers.instruction(null, "en"))
        assertEquals("Has llegado a tu destino", Maneuvers.instruction(null, "es"))
    }

    @Test
    fun `distance uses regional decimal separator without changing units`() {
        assertEquals("2.4 km", Format.distanceText(Format.UNIT_KMH, 2400.0, Locale.US))
        assertEquals("2,4 km", Format.distanceText(Format.UNIT_KMH, 2400.0, Locale.forLanguageTag("es-ES")))
        assertEquals("350 m", Format.distanceText(Format.UNIT_KMH, 350.0, Locale.forLanguageTag("es-ES")))
    }
}
