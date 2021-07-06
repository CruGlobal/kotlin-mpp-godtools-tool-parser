package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.UIKit.UIColor

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Color = UIColor

internal actual inline fun color(red: Int, green: Int, blue: Int, alpha: Double) = UIColor(
    red = red.toDouble() / 255.0,
    green = green.toDouble() / 255.0,
    blue = blue.toDouble() / 255.0,
    alpha = alpha
)

internal actual fun Color.toRGB(): RGB = memScoped {
    val red = alloc<DoubleVar>()
    val green = alloc<DoubleVar>()
    val blue = alloc<DoubleVar>()
    val alpha = alloc<DoubleVar>()
    getRed(red.ptr, green.ptr, blue.ptr, alpha.ptr)
    return RGB(red.value, green.value, blue.value, alpha.value)
}
