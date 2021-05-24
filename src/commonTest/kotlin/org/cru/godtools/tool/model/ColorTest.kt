package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunOnAndroidWith(AndroidJUnit4::class)
class ColorTest {
    private val red = color(255, 0, 0, 1.0)
    private val green = color(0, 255, 0, 1.0)
    private val blue = color(0, 0, 255, 1.0)
    private val black = color(0, 0, 0, 1.0)
    private val transparent = color(0, 0, 0, 0.0)

    @Test
    fun testParseColor() {
        assertEquals(red, "rgba(255,0,0,1)".toColorOrNull())
        assertEquals(green, "rgba(0,255,0,1)".toColorOrNull())
        assertEquals(blue, "rgba(0,0,255,1)".toColorOrNull())
        assertEquals(black, "rgba(0,0,0,1)".toColorOrNull())
        assertEquals(black, "rgba(0,0,0,1.0)".toColorOrNull())
        assertEquals(transparent, "rgba(0,0,0,0)".toColorOrNull())
    }

    @Test
    fun testParseColorInvalid() {
        assertNull("akjsdf".toColorOrNull())
        assertNull("rgba(-1,0,0,0)".toColorOrNull())
        assertNull("rgba(256,0,0,1)".toColorOrNull())
        assertNull("rgba(0,-1,0,0)".toColorOrNull())
        assertNull("rgba(0,256,0,1)".toColorOrNull())
        assertNull("rgba(0,0,-1,0)".toColorOrNull())
        assertNull("rgba(0,0,256,1)".toColorOrNull())
        assertNull("rgba(0,0,0,-0.1)".toColorOrNull())
        assertNull("rgba(0,0,0,1.1)".toColorOrNull())
    }
}
