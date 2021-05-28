package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.Manifest.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.Text.Align.Companion.toTextAlignOrNull
import org.cru.godtools.tool.model.Text.Style.Companion.toTextStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TextTest {
    private val parent = object : Styles {
        override val stylesParent = null
        override val manifest get() = TODO()

        override var textScale = DEFAULT_TEXT_SCALE
    }

    @Test
    fun testTextScale() {
        assertEquals(DEFAULT_TEXT_SCALE, Text(parent).textScale, 0.001)
        assertEquals(2.0, Text(parent, textScale = 2.0).textScale, 0.001)

        parent.textScale = 3.0
        assertEquals(3.0, Text(parent).textScale, 0.001)
        assertEquals(6.0, Text(parent, textScale = 2.0).textScale, 0.001)
    }

    @Test
    fun testTextAlignParsing() {
        assertEquals(Text.Align.START, "start".toTextAlignOrNull())
        assertEquals(Text.Align.CENTER, "center".toTextAlignOrNull())
        assertEquals(Text.Align.END, "end".toTextAlignOrNull())
        assertNull("jaksldf".toTextAlignOrNull())
    }

    @Test
    fun testTextStyleParsing() {
        assertEquals(setOf(Text.Style.BOLD, Text.Style.ITALIC), "bold italic".toTextStyles())
        assertEquals(setOf(Text.Style.UNDERLINE), "underline underline".toTextStyles())
        assertTrue("ajksdf jalkwer".toTextStyles().isEmpty())
    }
}
