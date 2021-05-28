package org.cru.godtools.tool.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageGravityTest {
    @Test
    fun verifyParse() {
        with(ImageGravity.parseOrNull("start unrecognized center")!!) {
            assertFalse(isCenterX)
            assertTrue(isStart)
            assertFalse(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertFalse(isCenter)
        }

        with(ImageGravity.parseOrNull("center end")!!) {
            assertFalse(isCenterX)
            assertFalse(isStart)
            assertTrue(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertFalse(isCenter)
        }

        with(ImageGravity.parseOrNull("center")!!) {
            assertTrue(isCenterX)
            assertFalse(isStart)
            assertFalse(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertTrue(isCenter)
        }

        with(ImageGravity.parseOrNull("start top center")!!) {
            assertTrue(isStart)
            assertFalse(isEnd)
            assertFalse(isCenterX)
            assertTrue(isTop)
            assertFalse(isBottom)
            assertFalse(isCenterY)
        }
    }

    @Test
    fun verifyParseConflictingGravity() {
        assertNull(ImageGravity.parseOrNull("start end"))
        assertNull(ImageGravity.parseOrNull("end start"))
        assertNull(ImageGravity.parseOrNull("start top end"))
        assertNull(ImageGravity.parseOrNull("top bottom end"))
        assertNull(ImageGravity.parseOrNull("bottom top end"))
    }
}
