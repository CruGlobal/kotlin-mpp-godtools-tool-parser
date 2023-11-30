package org.cru.godtools.shared.tool.parser.model

import kotlin.test.assertEquals
import org.junit.Test

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

    @Test
    fun testPropertyIconGravity() {
        with(null as Button?) {
            assertEquals(Button.DEFAULT_ICON_GRAVITY, iconGravity)
        }

        with(Button(iconGravity = Gravity.Horizontal.END) as Button?) {
            assertEquals(Gravity.Horizontal.END, iconGravity)
        }
    }

    @Test
    fun testPropertyIconSize() {
        with(null as Button?) {
            assertEquals(Button.DEFAULT_ICON_SIZE, iconSize)
        }

        with(Button(iconSize = 9) as Button?) {
            assertEquals(9, iconSize)
        }
    }
}
