package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.model.RGB
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
        val color = RGB(.25, .5, .75, .5).toPlatformColor()
        val (r, g, b) = color.rgb()

        assertEquals(.25, r, 0.0000001)
        assertEquals(.5, g, 0.0000001)
        assertEquals(.75, b, 0.0000001)
        assertEquals(0.5, color.alpha, 0.0000001)
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
