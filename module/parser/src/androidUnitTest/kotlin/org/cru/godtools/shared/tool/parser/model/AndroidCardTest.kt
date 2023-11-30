package org.cru.godtools.shared.tool.parser.model

import kotlin.test.assertEquals
import org.junit.Test

class AndroidCardTest {
    @Test
    fun testPropertyBackgroundColor() {
        with(null as Card?) {
            assertEquals(Manifest.DEFAULT_BACKGROUND_COLOR, backgroundColor)
        }

        val parent = Manifest(cardBackgroundColor = TestColors.RANDOM)
        with(Card(parent) as Card?) {
            assertEquals(parent.cardBackgroundColor, backgroundColor)
        }
        with(Card(parent, backgroundColor = TestColors.GREEN) as Card?) {
            assertEquals(TestColors.GREEN, backgroundColor)
        }
    }
}
