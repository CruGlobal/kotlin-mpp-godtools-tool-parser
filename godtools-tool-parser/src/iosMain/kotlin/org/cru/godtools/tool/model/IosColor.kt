package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.UIKit.UIColor

@Suppress("CONFLICTING_OVERLOADS")
actual typealias PlatformColor = UIColor

internal actual fun RGB.toPlatformColor() = UIColor(
    red = r.toDouble() / 255.0,
    green = g.toDouble() / 255.0,
    blue = b.toDouble() / 255.0,
    alpha = a.toDouble()
)
internal actual fun PlatformColor.toRGB(): RGB = memScoped {
    val red = alloc<DoubleVar>()
    val green = alloc<DoubleVar>()
    val blue = alloc<DoubleVar>()
    val alpha = alloc<DoubleVar>()
    getRed(red.ptr, green.ptr, blue.ptr, alpha.ptr)
    return RGB(red.value, green.value, blue.value, alpha.value)
}
