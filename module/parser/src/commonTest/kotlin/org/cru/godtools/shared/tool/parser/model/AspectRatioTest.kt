package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cru.godtools.shared.tool.parser.model.AspectRatio.Companion.toAspectRatioOrNull

class AspectRatioTest {
    @Test
    fun testRatio() {
        assertEquals(1.778, AspectRatio(16, 9).ratio, 0.001)
        assertEquals(1.0, AspectRatio(1, 1).ratio, 0.0001)
    }

    @Test
    fun testParsing() {
        assertEquals(AspectRatio(16, 9), "16:9".toAspectRatioOrNull())
        assertNull("1".toAspectRatioOrNull())
        assertNull("1:2:3".toAspectRatioOrNull())
        assertNull("a".toAspectRatioOrNull())
        assertNull("1:a".toAspectRatioOrNull())
        assertNull((null as String?).toAspectRatioOrNull())
    }
}
