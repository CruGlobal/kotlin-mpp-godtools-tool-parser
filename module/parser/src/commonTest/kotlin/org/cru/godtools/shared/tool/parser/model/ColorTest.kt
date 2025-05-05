package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
    @Test
    fun testToRGB() {
        assertEquals(TestColors.RED, TestColors.RED.toRGB().toPlatformColor())
        assertEquals(TestColors.GREEN, TestColors.GREEN.toRGB().toPlatformColor())
        assertEquals(TestColors.BLUE, TestColors.BLUE.toRGB().toPlatformColor())
        assertEquals(TestColors.BLACK, TestColors.BLACK.toRGB().toPlatformColor())
        assertEquals(TRANSPARENT, TRANSPARENT.toRGB().toPlatformColor())
    }
}
