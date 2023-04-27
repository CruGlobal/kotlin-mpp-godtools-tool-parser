package org.cru.godtools.shared.tool.parser.model

import org.junit.Test
import kotlin.test.assertEquals

class AndroidButtonTest {
    @Test
    fun testPropertyGravity() {
        with(null as Button?) {
            assertEquals(Button.DEFAULT_GRAVITY, gravity)
        }

        with(Button(gravity = Gravity.Horizontal.END) as Button?) {
            assertEquals(Gravity.Horizontal.END, gravity)
        }
    }

    @Test
    fun testPropertyWidth() {
        with(null as Button?) {
            assertEquals(Button.DEFAULT_WIDTH, width)
        }

        with(Button(width = Dimension.Pixels(50)) as Button?) {
            assertEquals(Dimension.Pixels(50), width)
        }
    }
}
