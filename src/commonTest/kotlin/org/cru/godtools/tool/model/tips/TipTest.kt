package org.cru.godtools.tool.model.tips

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.tips.Tip.Type.Companion.toTypeOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class TipTest : UsesResources("model/tips") {
    @Test
    fun verifyParse() {
        val tip = Tip(Manifest(), "name", getTestXmlParser("tip.xml"))
        assertEquals("name", tip.id)
        assertEquals(Tip.Type.ASK, tip.type)
        assertEquals(2, tip.pages.size)
        assertEquals(0, tip.pages[0].position)
        assertFalse(tip.pages[0].isLastPage)
        assertEquals("Page 1", assertIs<Text>(tip.pages[0].content[0]).text)
        assertEquals(1, tip.pages[1].position)
        assertTrue(tip.pages[1].isLastPage)
        assertEquals("Page 2", assertIs<Text>(tip.pages[1].content[0]).text)
    }

    // region Tip.Type
    @Test
    fun testParseTipType() {
        assertEquals(Tip.Type.TIP, "tip".toTypeOrNull())
        assertEquals(Tip.Type.ASK, "ask".toTypeOrNull())
        assertEquals(Tip.Type.CONSIDER, "consider".toTypeOrNull())
        assertEquals(Tip.Type.PREPARE, "prepare".toTypeOrNull())
        assertEquals(Tip.Type.QUOTE, "quote".toTypeOrNull())
        assertNull("klasjdfjalewr".toTypeOrNull())
    }
    // endregion Tip.Type
}
