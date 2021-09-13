package org.cru.godtools.tool.model

import android.graphics.Color
import androidx.annotation.ColorInt
import com.github.ajalt.colormath.model.RGB

actual typealias PlatformColor = Int

@ColorInt
internal actual fun RGB.toPlatformColor() = Color.argb(alphaInt, redInt, greenInt, blueInt)
internal actual fun PlatformColor.toRGB(): RGB =
    RGB.from255(Color.red(this), Color.green(this), Color.blue(this), Color.alpha(this))
