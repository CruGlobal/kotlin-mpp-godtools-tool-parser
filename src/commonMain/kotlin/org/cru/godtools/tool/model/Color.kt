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
        unsafeColor(red.toInt(), green.toInt(), blue.toInt(), alpha.toDouble())
    } catch (ignored: Exception) {
        null
    }
}

@AndroidColorInt
private fun unsafeColor(red: Int, green: Int, blue: Int, alpha: Double): Color {
    require(red in 0..255) { "Invalid red value" }
    require(green in 0..255) { "Invalid green value" }
    require(blue in 0..255) { "Invalid green value" }
    require(alpha in 0.0..1.0) { "Invalid alpha value" }
    return color(red, green, blue, alpha)
}

@AndroidColorInt
internal expect inline fun color(red: Int, green: Int, blue: Int, alpha: Double): Color

internal expect fun Color.toRGB(): RGB
internal fun RGB.toColor() = color(r, g, b, a.toDouble())
