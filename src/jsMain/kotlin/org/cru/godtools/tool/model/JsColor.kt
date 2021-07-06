package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.fromCss
import com.github.ajalt.colormath.toCssRgb

actual typealias Color = String

internal actual fun RGB.toColor() = toCssRgb(namedRgba = true, renderAlpha = RenderCondition.ALWAYS)
internal actual fun Color.toRGB(): RGB = com.github.ajalt.colormath.Color.fromCss(this).toRGB()
