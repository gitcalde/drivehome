package com.minimal.carlauncher.nav

/** Turns OSRM manoeuvres into a glanceable arrow and a short English instruction. */
object Maneuvers {

    fun glyph(step: RouteStep?): String {
        if (step == null) return "◉"
        return when (step.type) {
            "arrive" -> "◉"
            "roundabout", "rotary", "roundabout turn", "exit roundabout", "exit rotary" -> "⟳"
            else -> when (step.modifier) {
                "uturn" -> "↶"
                "sharp left" -> "↙"
                "left" -> "←"
                "slight left" -> "↖"
                "slight right" -> "↗"
                "right" -> "→"
                "sharp right" -> "↘"
                else -> "↑"
            }
        }
    }

    fun instruction(step: RouteStep?, language: String = "en"): String {
        if (language != "en") return spanishInstruction(step)
        if (step == null) return "Arrive at destination"
        val onto = if (step.roadName.isNotBlank()) " onto ${step.roadName}" else ""
        val direction = step.modifier?.let { mod ->
            when (mod) {
                "uturn" -> "Make a U-turn"
                "straight" -> "Continue straight"
                else -> "Turn $mod"
            }
        } ?: "Continue"
        return when (step.type) {
            "arrive" -> "Arrive at destination"
            "roundabout", "rotary" -> {
                val exit = step.roundaboutExit?.let { "take the ${ordinal(it)} exit" } ?: "go through"
                "At the roundabout, $exit$onto"
            }
            "merge" -> "Merge$onto"
            "on ramp" -> "Take the ramp$onto"
            "off ramp" -> "Take the exit$onto"
            "fork" -> "Keep ${step.modifier?.replace("slight ", "") ?: "straight"}$onto"
            "end of road" -> "At the end of the road, ${direction.lowercase()}$onto"
            "continue", "new name" -> "Continue$onto"
            else -> "$direction$onto"
        }
    }

    private fun spanishInstruction(step: RouteStep?): String {
        if (step == null || step.type == "arrive") return "Has llegado a tu destino"
        val onto = if (step.roadName.isNotBlank()) " hacia ${step.roadName}" else ""
        val direction = when (step.modifier) {
            "uturn" -> "Cambia de sentido"
            "left" -> "Gira a la izquierda"
            "right" -> "Gira a la derecha"
            "slight left" -> "Gira ligeramente a la izquierda"
            "slight right" -> "Gira ligeramente a la derecha"
            "sharp left" -> "Gira pronunciadamente a la izquierda"
            "sharp right" -> "Gira pronunciadamente a la derecha"
            "straight" -> "Continúa recto"
            else -> "Continúa"
        }
        return when (step.type) {
            "roundabout", "rotary" -> {
                val exit = step.roundaboutExit?.let { "toma la salida $it" } ?: "atraviesa la rotonda"
                "En la rotonda, $exit$onto"
            }
            "merge" -> "Incorpórate$onto"
            "on ramp" -> "Toma el acceso$onto"
            "off ramp" -> "Toma la salida$onto"
            "fork" -> when (step.modifier) {
                "left", "slight left", "sharp left" -> "Mantente a la izquierda$onto"
                "right", "slight right", "sharp right" -> "Mantente a la derecha$onto"
                else -> "Continúa recto$onto"
            }
            "end of road" -> "Al final de la vía, ${direction.lowercase()}$onto"
            "continue", "new name" -> "Continúa$onto"
            else -> "$direction$onto"
        }
    }

    private fun ordinal(n: Int): String = when {
        n % 100 in 11..13 -> "${n}th"
        n % 10 == 1 -> "${n}st"
        n % 10 == 2 -> "${n}nd"
        n % 10 == 3 -> "${n}rd"
        else -> "${n}th"
    }
}
