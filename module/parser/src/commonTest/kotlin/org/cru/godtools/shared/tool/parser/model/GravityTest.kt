package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.model.Gravity.Vertical.Companion.toVerticalGravityOrNull

class GravityTest {
    @Test
    fun verifyParse() {
        with("start unrecognized center".toGravityOrNull()!!) {
            assertFalse(isCenterX)
            assertTrue(isStart)
            assertFalse(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertFalse(isCenter)
            assertEquals(Gravity.Horizontal.START, horizontal)
            assertEquals(Gravity.Vertical.CENTER, vertical)
        }

        with("center end".toGravityOrNull()!!) {
            assertFalse(isCenterX)
            assertFalse(isStart)
            assertTrue(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertFalse(isCenter)
            assertEquals(Gravity.Horizontal.END, horizontal)
            assertEquals(Gravity.Vertical.CENTER, vertical)
        }

        with("center".toGravityOrNull()!!) {
            assertTrue(isCenterX)
            assertFalse(isStart)
            assertFalse(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertTrue(isCenter)
            assertEquals(Gravity.Horizontal.CENTER, horizontal)
            assertEquals(Gravity.Vertical.CENTER, vertical)
        }

        with("start top center".toGravityOrNull()!!) {
            assertTrue(isStart)
            assertFalse(isEnd)
            assertFalse(isCenterX)
            assertTrue(isTop)
            assertFalse(isBottom)
            assertFalse(isCenterY)
            assertEquals(Gravity.Horizontal.START, horizontal)
            assertEquals(Gravity.Vertical.TOP, vertical)
        }

        with("top center".toGravityOrNull()!!) {
            assertFalse(isCenter)
            assertEquals(Gravity.Vertical.TOP, vertical)
            assertEquals(Gravity.Horizontal.CENTER, horizontal)
        }

        with("end bottom".toGravityOrNull()!!) {
            assertFalse(isStart)
            assertTrue(isEnd)
            assertFalse(isCenterX)
            assertFalse(isTop)
            assertTrue(isBottom)
            assertFalse(isCenterY)
            assertFalse(isCenter)
            assertEquals(Gravity.Horizontal.END, horizontal)
            assertEquals(Gravity.Vertical.BOTTOM, vertical)
        }
    }

    @Test
    fun verifyParseInvalidGravity() {
        assertNull((null as String?).toGravityOrNull())
        assertNull("start end".toGravityOrNull())
        assertNull("end start".toGravityOrNull())
        assertNull("start top end".toGravityOrNull())
        assertNull("top bottom end".toGravityOrNull())
        assertNull("bottom top end".toGravityOrNull())
    }

    // region Gravity.Vertical
    @Test
    fun `Vertical - toVerticalGravityOrNull - parses vertical gravity values`() {
        assertEquals(Gravity.Vertical.TOP, "top".toVerticalGravityOrNull())
        assertEquals(Gravity.Vertical.CENTER, "center".toVerticalGravityOrNull())
        assertEquals(Gravity.Vertical.BOTTOM, "bottom".toVerticalGravityOrNull())
    }

    @Test
    fun `Vertical - toVerticalGravityOrNull - returns null for invalid values`() {
        assertNull("start".toVerticalGravityOrNull(), "horizontal gravity values are not valid vertical gravities")
        assertNull("end".toVerticalGravityOrNull(), "horizontal gravity values are not valid vertical gravities")
        assertNull("top bottom".toVerticalGravityOrNull(), "gravity sequences are not valid vertical gravities")
        assertNull("jasdf".toVerticalGravityOrNull(), "unrecognized values are not valid vertical gravities")
    }
    // endregion Gravity.Vertical
}
