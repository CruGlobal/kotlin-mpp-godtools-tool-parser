package org.cru.godtools.tool.model

import com.github.ajalt.colormath.model.RGB
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.UIKit.UIColor

// HACK: This should be in IosColor in iosMain, but colormath does not support the hierarchical multiplatform model yet.
internal actual fun RGB.toPlatformColor() = UIColor(
    red = r.toDouble(),
    green = g.toDouble(),
    blue = b.toDouble(),
    alpha = alpha.toDouble()
)
internal actual fun PlatformColor.toRGB(): RGB = memScoped {
    val red = alloc<DoubleVar>()
    val green = alloc<DoubleVar>()
    val blue = alloc<DoubleVar>()
    val alpha = alloc<DoubleVar>()
    getRed(red.ptr, green.ptr, blue.ptr, alpha.ptr)
    return RGB(red.value, green.value, blue.value, alpha.value)
}
