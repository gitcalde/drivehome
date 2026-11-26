package com.minimal.carlauncher.core

/** Inverted luminance with a cool bias and a small blue/red chroma contribution.
 * Pure coefficients, so the transform can be tested without Android graphics classes.
 */
object NightMapPalette {
    fun matrix(): FloatArray {
        val r = -0.76f * 0.2126f
        val g = -0.76f * 0.7152f
        val b = -0.76f * 0.0722f
        return floatArrayOf(
            r + 0.06f, g, b - 0.06f, 200f,
            r, g, b, 207f,
            r - 0.10f, g, b + 0.10f, 218f,
            0f, 0f, 0f, 1f, 0f
        )
    }
}
