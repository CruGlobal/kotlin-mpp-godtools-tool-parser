package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.cru.godtools.shared.tool.parser.model.Dimension.Companion.toDimensionOrNull

internal const val DIMENSION_TOLERANCE = 0.0001f

class DimensionTest {
    @Test
    fun testToDimensionOrNullInvalid() {
        assertNull((null as String?).toDimensionOrNull())
        assertNull("".toDimensionOrNull())

        assertNull("-1%".toDimensionOrNull())
        assertNull("101%".toDimensionOrNull())

        assertNull("-1".toDimensionOrNull())
        assertNull("12a".toDimensionOrNull())

        // Overflow Test
        assertNull("999999999999999999999".toDimensionOrNull())
    }

    @Test
    fun testToDimensionOrNullValidPercentages() {
        assertEquals(0f, assertIs<Dimension.Percent>("0%".toDimensionOrNull()).value, DIMENSION_TOLERANCE)
        assertEquals(.5f, assertIs<Dimension.Percent>("50%".toDimensionOrNull()).value, DIMENSION_TOLERANCE)
        assertEquals(1f, assertIs<Dimension.Percent>("100%".toDimensionOrNull()).value, DIMENSION_TOLERANCE)
        assertEquals(1f, assertIs<Dimension.Percent>(" 100%".toDimensionOrNull()).value, DIMENSION_TOLERANCE)
        assertEquals(1f, assertIs<Dimension.Percent>("100% ".toDimensionOrNull()).value, DIMENSION_TOLERANCE)
        assertEquals(1f, assertIs<Dimension.Percent>("\t 100%\t ".toDimensionOrNull()).value, DIMENSION_TOLERANCE)
    }

    @Test
    fun testToDimensionOrNullValidPixels() {
        assertEquals(0, assertIs<Dimension.Pixels>("0".toDimensionOrNull()).value)
        assertEquals(37, assertIs<Dimension.Pixels>("37".toDimensionOrNull()).value)
        assertEquals(1234, assertIs<Dimension.Pixels>("1234".toDimensionOrNull()).value)
        assertEquals(1, assertIs<Dimension.Pixels>(" \t1".toDimensionOrNull()).value)
        assertEquals(1, assertIs<Dimension.Pixels>("1\t ".toDimensionOrNull()).value)
        assertEquals(1, assertIs<Dimension.Pixels>(" \t1\t ".toDimensionOrNull()).value)
    }
}
