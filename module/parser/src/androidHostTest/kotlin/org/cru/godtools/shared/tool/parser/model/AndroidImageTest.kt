package org.cru.godtools.shared.tool.parser.model

import kotlin.test.assertEquals
import org.junit.Test

class AndroidImageTest {
    @Test
    fun testWidthAndGravity() {
        with(null as Image?) {
            assertEquals(Image.DEFAULT_WIDTH, width)
            assertEquals(Image.DEFAULT_GRAVITY, gravity)
        }

        with(Image() as Image?) {
            assertEquals(Image.DEFAULT_WIDTH, width)
            assertEquals(Image.DEFAULT_GRAVITY, gravity)
        }

        with(Image(width = Dimension.Pixels(20), gravity = Gravity.Horizontal.END) as Image?) {
            assertEquals(Dimension.Pixels(20), width)
            assertEquals(Gravity.Horizontal.END, gravity)
        }
    }
}
