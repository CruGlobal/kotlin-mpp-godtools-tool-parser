package org.cru.godtools.shared.tool.parser.model

import org.junit.Test
import kotlin.test.assertEquals

class AndroidMultiselectTest {
    @Test
    fun testOptionPropertyBackgroundColor() {
        with(null as Multiselect.Option?) {
            assertEquals(stylesParent.multiselectOptionBackgroundColor, backgroundColor)
        }

        with(Multiselect.Option(backgroundColor = TestColors.GREEN) as Multiselect.Option?) {
            assertEquals(TestColors.GREEN, backgroundColor)
        }
    }

    @Test
    fun testOptionPropertySelectedColor() {
        with(null as Multiselect.Option?) {
            assertEquals(stylesParent.defaultSelectedColor, selectedColor)
        }

        with(Multiselect.Option(selectedColor = TestColors.GREEN) as Multiselect.Option?) {
            assertEquals(TestColors.GREEN, selectedColor)
        }
    }
}
