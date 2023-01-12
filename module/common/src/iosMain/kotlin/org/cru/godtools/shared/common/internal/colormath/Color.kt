package org.cru.godtools.shared.common.internal.colormath

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGB
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.UIKit.UIColor

fun Color.toUIColor() = toSRGB().let {
    UIColor(
        red = it.r.toDouble(),
        green = it.g.toDouble(),
        blue = it.b.toDouble(),
        alpha = alpha.toDouble()
    )
}

fun UIColor.toColormathSRGB(): RGB = memScoped {
    val red = alloc<DoubleVar>()
    val green = alloc<DoubleVar>()
    val blue = alloc<DoubleVar>()
    val alpha = alloc<DoubleVar>()
    getRed(red.ptr, green.ptr, blue.ptr, alpha.ptr)
    return RGB(red.value, green.value, blue.value, alpha.value)
}
