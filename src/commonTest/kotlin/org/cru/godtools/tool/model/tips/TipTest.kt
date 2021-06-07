package org.cru.godtools.tool.model.tips

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
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
}
