package org.cru.godtools.tool.model

import androidx.annotation.ColorInt
import com.github.ajalt.colormath.RGB

actual typealias PlatformColor = Int

@ColorInt
internal actual fun RGB.toPlatformColor() = toPackedInt()
internal actual fun PlatformColor.toRGB() = RGB.fromInt(this)
