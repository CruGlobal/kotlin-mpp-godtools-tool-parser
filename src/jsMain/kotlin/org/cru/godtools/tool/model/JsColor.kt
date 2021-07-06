package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.fromCss

actual typealias Color = String

internal actual inline fun color(red: Int, green: Int, blue: Int, alpha: Double) =
    "rgba($red,$green,$blue,$alpha)"

internal actual fun Color.toRGB(): RGB  = com.github.ajalt.colormath.Color.fromCss(this).toRGB()
