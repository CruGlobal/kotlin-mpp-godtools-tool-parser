package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
    @Test
    fun testToRGB() {
        assertEquals(TestColors.RED, TestColors.RED.toPlatformColor().toRGB())
        assertEquals(TestColors.GREEN, TestColors.GREEN.toPlatformColor().toRGB())
        assertEquals(TestColors.BLUE, TestColors.BLUE.toPlatformColor().toRGB())
        assertEquals(TestColors.BLACK, TestColors.BLACK.toPlatformColor().toRGB())
        assertEquals(TRANSPARENT, TRANSPARENT.toPlatformColor().toRGB())
    }
}
