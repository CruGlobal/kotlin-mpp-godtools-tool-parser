package org.cru.godtools.tool.model

import androidx.annotation.ColorInt

actual typealias Color = Int

@ColorInt
internal actual inline fun color(red: Int, green: Int, blue: Int, alpha: Double) =
    android.graphics.Color.argb((alpha * 255).toInt(), red, green, blue)
