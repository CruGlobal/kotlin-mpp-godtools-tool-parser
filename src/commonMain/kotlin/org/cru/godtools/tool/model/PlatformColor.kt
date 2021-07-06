package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import org.cru.godtools.tool.internal.AndroidColorInt
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
private val COLOR_REGEX =
    Regex("^\\s*rgba\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9.]+)\\s*\\)\\s*$")

expect class PlatformColor

@AndroidColorInt
internal fun String.toColorOrNull(): PlatformColor? = COLOR_REGEX.matchEntire(this)?.let {
    try {
        val (red, green, blue, alpha) = it.destructured
        RGB(red.toInt(), green.toInt(), blue.toInt(), alpha.toFloat()).toPlatformColor()
    } catch (ignored: IllegalArgumentException) {
        null
    }
}

@AndroidColorInt
internal inline fun color(red: Int, green: Int, blue: Int, alpha: Double) =
    RGB(red, green, blue, alpha.toFloat()).toPlatformColor()

internal expect fun PlatformColor.toRGB(): RGB
internal expect fun RGB.toPlatformColor(): PlatformColor
