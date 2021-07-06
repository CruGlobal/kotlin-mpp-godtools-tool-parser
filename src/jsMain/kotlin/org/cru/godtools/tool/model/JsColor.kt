package org.cru.godtools.tool.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.fromCss
import com.github.ajalt.colormath.toCssRgb

actual typealias PlatformColor = String

internal actual fun RGB.toPlatformColor() = toCssRgb(namedRgba = true, renderAlpha = RenderCondition.ALWAYS)
internal actual fun PlatformColor.toRGB() = Color.fromCss(this).toRGB()
