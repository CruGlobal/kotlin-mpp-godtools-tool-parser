package org.cru.godtools.shared.tool.parser.internal

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGB

private val COLOR_REGEX =
    Regex("^\\s*rgba\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9.]+)\\s*\\)\\s*$")
private val VALID_RGB = 0..255
private val VALID_ALPHA = 0f..1f

internal fun String.toColorOrNull(): Color? = COLOR_REGEX.matchEntire(this)?.let {
    try {
        val red = it.groupValues[1].toInt().also { require(it in VALID_RGB) }
        val green = it.groupValues[2].toInt().also { require(it in VALID_RGB) }
        val blue = it.groupValues[3].toInt().also { require(it in VALID_RGB) }
        val alpha = it.groupValues[4].toDouble().also { require(it in VALID_ALPHA) }
        color(red, green, blue, alpha)
    } catch (ignored: IllegalArgumentException) {
        null
    }
}

internal fun color(red: Int, green: Int, blue: Int, alpha: Double): Color =
    RGB(red / 255f, green / 255f, blue / 255f, alpha)
