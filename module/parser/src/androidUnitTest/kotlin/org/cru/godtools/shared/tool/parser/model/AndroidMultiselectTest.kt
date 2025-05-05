package org.cru.godtools.shared.tool.parser.model

import kotlin.test.assertEquals
import org.junit.Test

class AndroidMultiselectTest {
    @Test
    fun testOptionPropertyBackgroundColor() {
        with(null as Multiselect.Option?) {
            assertEquals(stylesParent.multiselectOptionBackgroundColor, backgroundColor)
        }

        with(Multiselect.Option(backgroundColor = TestColors.GREEN.toPlatformColor()) as Multiselect.Option?) {
            assertEquals(TestColors.GREEN.toPlatformColor(), backgroundColor)
        }
    }

    @Test
    fun testOptionPropertySelectedColor() {
        with(null as Multiselect.Option?) {
            assertEquals(stylesParent.defaultSelectedColor, selectedColor)
        }

        with(Multiselect.Option(selectedColor = TestColors.GREEN.toPlatformColor()) as Multiselect.Option?) {
            assertEquals(TestColors.GREEN.toPlatformColor(), selectedColor)
        }
    }
}
