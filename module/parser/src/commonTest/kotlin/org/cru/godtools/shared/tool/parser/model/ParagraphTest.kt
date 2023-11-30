package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.withDeviceType

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ParagraphTest : UsesResources() {
    @Test
    fun testParseParagraph() = runTest {
        val paragraph = Paragraph(Manifest(), getTestXmlParser("paragraph.xml"))
        assertEquals(2, paragraph.content.size)
        assertIs<Image>(paragraph.content[0])
        assertIs<Text>(paragraph.content[1])
    }

    @Test
    fun testParseParagraphIgnoredContent() = runTest {
        val manifest = Manifest(ParserConfig().withDeviceType(DeviceType.ANDROID))
        val paragraph = Paragraph(manifest, getTestXmlParser("paragraph_ignored_content.xml"))
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
