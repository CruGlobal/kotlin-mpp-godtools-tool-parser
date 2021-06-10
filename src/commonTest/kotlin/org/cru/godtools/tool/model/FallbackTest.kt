package org.cru.godtools.tool.model

import org.cru.godtools.tool.DEFAULT_SUPPORTED_DEVICE_TYPES
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.xml.XmlPullParserException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class FallbackTest : UsesResources() {
    @BeforeTest
    fun setupConfig() {
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.ANDROID)
    }

    @AfterTest
    fun resetConfig() {
        ParserConfig.supportedDeviceTypes = DEFAULT_SUPPORTED_DEVICE_TYPES
    }

    @Test
    fun testParseFallback() {
        val fallback = Fallback(Manifest(), getTestXmlParser("fallback.xml"))
        assertEquals(2, fallback.content.size)
        assertEquals("Test", assertIs<Text>(fallback.content[0]).text)
        assertEquals("Android", assertIs<Text>(fallback.content[1]).text)
    }

    @Test
    fun testParseParagraphFallback() {
        val fallback = Fallback(Manifest(), getTestXmlParser("fallback_paragraph.xml"))
        assertEquals(2, fallback.content.size)
        assertEquals("Test", assertIs<Text>(fallback.content[0]).text)
        assertEquals("Android", assertIs<Text>(fallback.content[1]).text)
    }

    @Test
    fun testParseParagraphFallbackInvalid() {
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
        assertTrue(Fallback(Manifest()).tips.isEmpty())
    }
}
