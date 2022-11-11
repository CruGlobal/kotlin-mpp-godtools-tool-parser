package org.cru.godtools.shared.tool.parser.model.tips

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.Tip.Type.Companion.toTypeOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TipTest : UsesResources("model/tips") {
    // region parseTip
    @Test
    fun testParseTipDefaults() = runTest {
        val tip = Tip(Manifest(), "test", getTestXmlParser("tip_defaults.xml"))
        assertEquals("test", tip.id)
        assertEquals(Tip.Type.DEFAULT, tip.type)
        assertEquals(0, tip.pages.size)
    }

    @Test
    fun testParseTip() = runTest {
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
    // region parseTip

    @Test
    fun testTipDefaults() {
        val manifest =
            Manifest(primaryColor = TestColors.RED, primaryTextColor = TestColors.RED, textColor = TestColors.RED)
        val tip = Tip(manifest)
        assertNotEquals(manifest.primaryColor, tip.primaryColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR, tip.primaryColor)
        assertNotEquals(manifest.primaryTextColor, tip.primaryTextColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_TEXT_COLOR, tip.primaryTextColor)
        assertNotEquals(manifest.textColor, tip.textColor)
        assertEquals(Manifest.DEFAULT_TEXT_COLOR, tip.textColor)
        assertEquals(Manifest.DEFAULT_TEXT_COLOR, (tip as Tip?).textColor)
        assertEquals(Manifest.DEFAULT_TEXT_COLOR, (null as Tip?).textColor)
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
