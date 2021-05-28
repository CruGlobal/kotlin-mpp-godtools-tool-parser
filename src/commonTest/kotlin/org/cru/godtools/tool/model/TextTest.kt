package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.Text.Align.Companion.toTextAlignOrNull
import org.cru.godtools.tool.model.Text.Style.Companion.toTextStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TextTest {
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
