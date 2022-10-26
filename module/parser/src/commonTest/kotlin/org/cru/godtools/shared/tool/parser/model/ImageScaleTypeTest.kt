package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageScaleTypeTest {
    @Test
    fun testParseOrNull() {
        assertEquals(ImageScaleType.FIT, "fit".toImageScaleTypeOrNull())
        assertEquals(ImageScaleType.FILL, "fill".toImageScaleTypeOrNull())
        assertEquals(ImageScaleType.FILL_X, "fill-x".toImageScaleTypeOrNull())
        assertEquals(ImageScaleType.FILL_Y, "fill-y".toImageScaleTypeOrNull())
        assertNull("ajklsdfjkaewr".toImageScaleTypeOrNull())
    }
}
