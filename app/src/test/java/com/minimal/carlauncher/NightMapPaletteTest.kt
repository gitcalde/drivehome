package com.minimal.carlauncher

import com.minimal.carlauncher.core.NightMapPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NightMapPaletteTest {
    private fun transform(r: Int, g: Int, b: Int): List<Float> {
        val m = NightMapPalette.matrix()
        return (0..2).map { i ->
            val j = i * 5
            (m[j] * r + m[j + 1] * g + m[j + 2] * b + m[j + 4]).coerceIn(0f, 255f)
        }
    }

    @Test
    fun `light land becomes dark and dark labels stay bright`() {
        val land = transform(242, 239, 233)
        val label = transform(30, 30, 30)
        assertTrue(land.all { it < 50 })
        assertTrue(label.zip(land).all { (light, dark) -> light - dark > 140 })
    }

    @Test
    fun `cyan water becomes cool rather than brown`() {
        val water = transform(170, 211, 223)
        assertTrue(water[2] > water[1])
        assertTrue(water[1] > water[0])
    }

    @Test
    fun `alpha remains unchanged`() {
        assertEquals(listOf(0f, 0f, 0f, 1f, 0f), NightMapPalette.matrix().takeLast(5))
    }
}
