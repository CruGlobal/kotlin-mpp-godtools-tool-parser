package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageGravityTest {
    @Test
    fun verifyParse() {
        with("start unrecognized center".toImageGravityOrNull()!!) {
            assertFalse(isCenterX)
            assertTrue(isStart)
            assertFalse(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertFalse(isCenter)
        }

        with("center end".toImageGravityOrNull()!!) {
            assertFalse(isCenterX)
            assertFalse(isStart)
            assertTrue(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertFalse(isCenter)
        }

        with("center".toImageGravityOrNull()!!) {
            assertTrue(isCenterX)
            assertFalse(isStart)
            assertFalse(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertTrue(isCenter)
        }

        with("start top center".toImageGravityOrNull()!!) {
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
        assertNull("start end".toImageGravityOrNull())
        assertNull("end start".toImageGravityOrNull())
        assertNull("start top end".toImageGravityOrNull())
        assertNull("top bottom end".toImageGravityOrNull())
        assertNull("bottom top end".toImageGravityOrNull())
    }
}
