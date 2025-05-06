package org.cru.godtools.shared.tool.parser.model

import kotlin.test.assertEquals
import org.junit.Test

class AndroidTextTest {
    @Test
    fun testPropertyTextAlign() {
        with(null as Text?) {
            assertEquals(Styles.DEFAULT_TEXT_ALIGN, textAlign)
        }

        val parent = Manifest().stylesOverride(textAlign = { Text.Align.CENTER })
        with(Text(parent = parent) as Text?) {
            assertEquals(Text.Align.CENTER, textAlign)
        }

        with(Text(parent = parent, textAlign = Text.Align.END) as Text?) {
            assertEquals(Text.Align.END, textAlign)
        }
    }

    @Test
    fun testPropertyTextColor() {
        val manifest = Manifest(textColor = TestColors.RANDOM)

        with(null as Text?) {
            assertEquals(Manifest.DEFAULT_TEXT_COLOR, textColor)
        }

        with(Text(manifest) as Text?) {
            assertEquals(manifest.textColor, textColor)
        }

        with(Text(manifest, textColor = TestColors.GREEN) as Text?) {
            assertEquals(TestColors.GREEN, textColor)
        }
    }

    @Test
    fun testPropertyTextScale() {
        with(null as Text?) {
            assertEquals(Styles.DEFAULT_TEXT_SCALE, textScale, 0.0001)
        }

        val parent = Manifest().stylesOverride(textScale = 2.0)
        with(Text(parent = parent) as Text?) {
            assertEquals(2.0, textScale, 0.0001)
        }

        with(Text(parent = parent, textScale = 3.0) as Text?) {
            assertEquals(6.0, textScale, 0.0001)
        }
    }
}
