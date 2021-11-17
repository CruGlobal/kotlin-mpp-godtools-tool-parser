package org.cru.godtools.tool.model

import com.github.ajalt.colormath.model.HSL
import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.tool.internal.AndroidColorInt
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
private val COLOR_REGEX =
    Regex("^\\s*rgba\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9.]+)\\s*\\)\\s*$")
@SharedImmutable
private val VALID_RGB = 0..255
@SharedImmutable
private val VALID_ALPHA = 0f..1f

expect class PlatformColor

@AndroidColorInt
internal fun String?.toColorOrNull(): PlatformColor? = this?.let { COLOR_REGEX.matchEntire(it) }?.let {
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

@AndroidColorInt
internal inline fun color(red: Int, green: Int, blue: Int, alpha: Double) =
    RGB(red / 255f, green / 255f, blue / 255f, alpha).toPlatformColor()

internal expect fun PlatformColor.toRGB(): RGB
internal expect fun RGB.toPlatformColor(): PlatformColor
internal fun PlatformColor.toHSL() = toRGB().toHSL()
internal fun HSL.toPlatformColor() = toSRGB().toPlatformColor()
