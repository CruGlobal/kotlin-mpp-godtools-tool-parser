package org.cru.godtools.tool.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageScaleTypeTest {
    @Test
    fun testParseOrNull() {
        assertEquals(ImageScaleType.FIT, ImageScaleType.parseOrNull("fit"))
        assertEquals(ImageScaleType.FILL, ImageScaleType.parseOrNull("fill"))
        assertEquals(ImageScaleType.FILL_X, ImageScaleType.parseOrNull("fill-x"))
        assertEquals(ImageScaleType.FILL_Y, ImageScaleType.parseOrNull("fill-y"))
        assertNull(ImageScaleType.parseOrNull(null))
        assertNull(ImageScaleType.parseOrNull("ajklsdfjkaewr"))
    }
}
