package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.withDeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FormTest : UsesResources() {
    @Test
    fun testParseForm() = runTest {
        val form = Form(Manifest(), getTestXmlParser("form.xml"))
        assertEquals(2, form.content.size)
        assertIs<Image>(form.content[0])
        assertIs<Text>(form.content[1])
    }

    @Test
    fun testParseParagraphIgnoredContent() = runTest {
        val manifest = Manifest(ParserConfig().withDeviceType(DeviceType.ANDROID))
        val form = Form(manifest, getTestXmlParser("form_ignored_content.xml"))
        assertEquals(1, form.content.size)
        assertIs<Text>(form.content[0])
    }

    @Test
    fun testTipsProperty() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip2"), Tip(it, "tip1")) })
        val form = Form(manifest) { listOf(InlineTip(it, "tip1"), InlineTip(it, "tip2")) }
        assertEquals(listOf(manifest.findTip("tip1")!!, manifest.findTip("tip2")!!), form.tips)
    }
}
