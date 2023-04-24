package org.cru.godtools.shared.tool.parser.model

import org.junit.Test
import kotlin.test.assertEquals

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
}
