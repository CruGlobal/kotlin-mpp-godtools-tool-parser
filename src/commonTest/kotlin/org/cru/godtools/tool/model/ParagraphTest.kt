package org.cru.godtools.tool.model

import org.cru.godtools.tool.DEFAULT_SUPPORTED_DEVICE_TYPES
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class ParagraphTest : UsesResources() {
    @BeforeTest
    fun setupConfig() {
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.ANDROID, DeviceType.MOBILE)
    }

    @AfterTest
    fun resetConfig() {
        ParserConfig.supportedDeviceTypes = DEFAULT_SUPPORTED_DEVICE_TYPES
    }

    @Test
    fun testParseParagraph() {
        val paragraph = Paragraph(Manifest(), getTestXmlParser("paragraph.xml"))
        assertEquals(2, paragraph.content.size)
        assertIs<Image>(paragraph.content[0])
        assertIs<Text>(paragraph.content[1])
    }

    @Test
    fun testParseParagraphIgnoredContent() {
        val paragraph = Paragraph(Manifest(), getTestXmlParser("paragraph_ignored_content.xml"))
        assertEquals(3, paragraph.content.size)
        assertEquals("Test", assertIs<Text>(paragraph.content[0]).text)
        assertEquals("Android", assertIs<Text>(paragraph.content[1]).text)
        assertEquals("Mobile", assertIs<Text>(paragraph.content[2]).text)
    }

    @Test
    fun testTipsProperty() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip2"), Tip(it, "tip1")) })
        val paragraph = Paragraph(manifest) { listOf(InlineTip(it, "tip1"), InlineTip(it, "tip2")) }
        assertEquals(listOf(manifest.findTip("tip1")!!, manifest.findTip("tip2")!!), paragraph.tips)
    }
}
