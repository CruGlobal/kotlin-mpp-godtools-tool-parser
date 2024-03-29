package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException

@RunOnAndroidWith(AndroidJUnit4::class)
class FallbackTest : UsesResources() {
    @Test
    fun testParseFallback() = runTest {
        val fallback = Fallback(Manifest(), getTestXmlParser("fallback.xml"))
        assertEquals(1, fallback.content.size)
        assertEquals("Test", assertIs<Text>(fallback.content.single()).text)
    }

    @Test
    fun testParseFallbackAllIgnored() = runTest {
        val fallback = Fallback(Manifest(), getTestXmlParser("fallback_all_ignored.xml"))
        assertTrue(fallback.content.isEmpty())
    }

    @Test
    fun testParseParagraphFallback() = runTest {
        val fallback = Fallback(Manifest(), getTestXmlParser("fallback_paragraph.xml"))
        assertEquals(1, fallback.content.size)
        assertEquals("Test", assertIs<Text>(fallback.content.single()).text)
    }

    @Test
    fun testParseParagraphFallbackAllIgnored() = runTest {
        val fallback = Fallback(Manifest(), getTestXmlParser("fallback_paragraph_all_ignored.xml"))
        assertTrue(fallback.content.isEmpty())
    }

    @Test
    fun testParseParagraphFallbackInvalid() = runTest {
        assertFailsWith(XmlPullParserException::class) {
            Fallback(Manifest(), getTestXmlParser("paragraph.xml"))
        }
    }

    @Test
    fun testTipsProperty() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1"), Tip(it, "tip2")) })
        val fallback = Fallback(manifest) { listOf(InlineTip(it, "tip1"), InlineTip(it, "tip2")) }
        assertEquals(manifest.findTip("tip1")!!, fallback.tips.single())
    }

    @Test
    fun testTipsPropertyNoChildren() {
        assertTrue(Fallback().tips.isEmpty())
    }
}
