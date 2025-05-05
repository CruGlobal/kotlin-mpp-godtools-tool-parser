package org.cru.godtools.shared.tool.parser.internal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cru.godtools.shared.tool.parser.model.TRANSPARENT
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.toPlatformColor

class ColorTest {
    @Test
    fun `toColorOrNull - Valid`() {
        assertEquals(TestColors.RED, "rgba(255,0,0,1)".toColorOrNull()?.toPlatformColor())
        assertEquals(TestColors.GREEN, "rgba(0,255,0,1)".toColorOrNull()?.toPlatformColor())
        assertEquals(TestColors.BLUE, "rgba(0,0,255,1)".toColorOrNull()?.toPlatformColor())
        assertEquals(TestColors.BLACK, "rgba(0,0,0,1)".toColorOrNull()?.toPlatformColor())
        assertEquals(TestColors.BLACK, "rgba(0,0,0,1.0)".toColorOrNull()?.toPlatformColor())
        assertEquals(TRANSPARENT, "rgba(0,0,0,0)".toColorOrNull()?.toPlatformColor())
    }

    @Test
    fun `toColorOrNull - Invalid`() {
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
