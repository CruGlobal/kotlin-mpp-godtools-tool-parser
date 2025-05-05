package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.model.HSL
import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.internal.color

private val COLOR_REGEX =
    Regex("^\\s*rgba\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9.]+)\\s*\\)\\s*$")
private val VALID_RGB = 0..255
private val VALID_ALPHA = 0f..1f

expect class PlatformColor

@AndroidColorInt
internal fun String?.toColorOrNull(): PlatformColor? = this?.let { COLOR_REGEX.matchEntire(it) }?.let {
    try {
        val red = it.groupValues[1].toInt().also { require(it in VALID_RGB) }
        val green = it.groupValues[2].toInt().also { require(it in VALID_RGB) }
        val blue = it.groupValues[3].toInt().also { require(it in VALID_RGB) }
        val alpha = it.groupValues[4].toDouble().also { require(it in VALID_ALPHA) }
        color(red, green, blue, alpha).toPlatformColor()
    } catch (ignored: IllegalArgumentException) {
        null
    }
}

internal expect fun PlatformColor.toRGB(): RGB
internal expect fun RGB.toPlatformColor(): PlatformColor
internal fun PlatformColor.toHSL() = toRGB().toHSL()
internal fun HSL.toPlatformColor() = toSRGB().toPlatformColor()
