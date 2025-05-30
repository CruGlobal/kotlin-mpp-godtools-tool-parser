package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.ColorInt
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.extensions.android.colorint.fromColorInt
import com.github.ajalt.colormath.extensions.android.colorint.toColorInt
import com.github.ajalt.colormath.model.RGB

actual typealias PlatformColor = Int

@ColorInt
internal actual fun Color.toPlatformColor() = toColorInt()
internal actual fun PlatformColor.toRGB() = RGB.fromColorInt(this)
