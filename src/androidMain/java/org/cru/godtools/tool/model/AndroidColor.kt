package org.cru.godtools.tool.model

import androidx.annotation.ColorInt
import com.github.ajalt.colormath.RGB

actual typealias Color = Int

@ColorInt
internal actual fun RGB.toColor() = toPackedInt()
internal actual fun Color.toRGB() = RGB.fromInt(this)
