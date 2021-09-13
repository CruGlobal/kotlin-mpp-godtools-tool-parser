package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlin.test.Test
import kotlin.test.assertEquals

class IosColorTest {
    @Test
    fun testToPlatformColor() {
        val color = RGB(1, 2, 3, .5f).toPlatformColor()
        val (r, g, b) = color.rgb()

        assertEquals(1 / 255.0, r)
        assertEquals(2 / 255.0, g)
        assertEquals(3 / 255.0, b)
        assertEquals(0.5, color.alpha, 0.000001)
    }

    private fun PlatformColor.rgb() = memScoped {
        val r = alloc<DoubleVar>()
        val g = alloc<DoubleVar>()
        val b = alloc<DoubleVar>()
        getRed(r.ptr, g.ptr, b.ptr, null)
        Triple(r.value, g.value, b.value)
    }
    private val PlatformColor.alpha get() = memScoped {
        with(alloc<DoubleVar>()) {
            getRed(null, null, null, ptr)
            value
        }
    }
}
