package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.Gravity.Companion.toGravityOrNull
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        }

        with("center end".toGravityOrNull()!!) {
            assertFalse(isCenterX)
            assertFalse(isStart)
            assertTrue(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertFalse(isCenter)
        }

        with("center".toGravityOrNull()!!) {
            assertTrue(isCenterX)
            assertFalse(isStart)
            assertFalse(isEnd)
            assertTrue(isCenterY)
            assertFalse(isTop)
            assertFalse(isBottom)
            assertTrue(isCenter)
        }

        with("start top center".toGravityOrNull()!!) {
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
        assertNull("start end".toGravityOrNull())
        assertNull("end start".toGravityOrNull())
        assertNull("start top end".toGravityOrNull())
        assertNull("top bottom end".toGravityOrNull())
        assertNull("bottom top end".toGravityOrNull())
    }
}