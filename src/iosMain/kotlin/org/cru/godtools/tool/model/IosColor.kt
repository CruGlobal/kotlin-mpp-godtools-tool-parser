package org.cru.godtools.tool.model

import platform.UIKit.UIColor

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Color = UIColor

internal actual inline fun color(red: Int, green: Int, blue: Int, alpha: Double) = UIColor(
    red = red.toDouble() / 255.0,
    green = green.toDouble() / 255.0,
    blue = blue.toDouble() / 255.0,
    alpha = alpha
)
