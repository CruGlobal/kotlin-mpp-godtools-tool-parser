package org.cru.godtools.shared.common.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.formatCssString

actual typealias PlatformColor = String

actual fun Color.toPlatformColor() = formatCssString(
    renderAlpha = RenderCondition.ALWAYS,
    legacyFormat = true,
    legacyName = true
)
