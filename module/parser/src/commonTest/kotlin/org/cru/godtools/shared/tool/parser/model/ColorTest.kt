package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ColorTest {
    @Test
    fun testParseColor() {
        assertEquals(TestColors.RED, "rgba(255,0,0,1)".toColorOrNull())
        assertEquals(TestColors.GREEN, "rgba(0,255,0,1)".toColorOrNull())
        assertEquals(TestColors.BLUE, "rgba(0,0,255,1)".toColorOrNull())
        assertEquals(TestColors.BLACK, "rgba(0,0,0,1)".toColorOrNull())
        assertEquals(TestColors.BLACK, "rgba(0,0,0,1.0)".toColorOrNull())
        assertEquals(TRANSPARENT, "rgba(0,0,0,0)".toColorOrNull())
    }

    @Test
    fun testParseColorInvalid() {
        assertNull((null as String?).toColorOrNull())
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

    @Test
    fun testToRGB() {
        assertEquals(TestColors.RED, TestColors.RED.toRGB().toPlatformColor())
        assertEquals(TestColors.GREEN, TestColors.GREEN.toRGB().toPlatformColor())
        assertEquals(TestColors.BLUE, TestColors.BLUE.toRGB().toPlatformColor())
        assertEquals(TestColors.BLACK, TestColors.BLACK.toRGB().toPlatformColor())
        assertEquals(TRANSPARENT, TRANSPARENT.toRGB().toPlatformColor())
    }
}
