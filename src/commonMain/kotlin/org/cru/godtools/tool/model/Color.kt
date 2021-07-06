package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import org.cru.godtools.tool.internal.AndroidColorInt
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
internal val COLOR_REGEX =
    Regex("^\\s*rgba\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9.]+)\\s*\\)\\s*$")

expect class Color

@AndroidColorInt
internal fun String.toColorOrNull(): Color? = COLOR_REGEX.matchEntire(this)?.let {
    try {
        val (red, green, blue, alpha) = it.destructured
        RGB(red.toInt(), green.toInt(), blue.toInt(), alpha.toFloat()).toColor()
    } catch (ignored: IllegalArgumentException) {
        null
    }
}

@AndroidColorInt
internal expect inline fun color(red: Int, green: Int, blue: Int, alpha: Double): Color

internal expect fun Color.toRGB(): RGB
internal fun RGB.toColor() = color(r, g, b, a.toDouble())
