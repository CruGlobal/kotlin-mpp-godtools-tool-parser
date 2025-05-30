package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.formatCssString
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.parse

actual typealias PlatformColor = String

internal actual fun Color.toPlatformColor() = toSRGB().formatCssString(
    renderAlpha = RenderCondition.ALWAYS,
    legacyFormat = true,
    legacyName = true
)
internal actual fun PlatformColor.toRGB(): RGB = Color.parse(this).toSRGB()
