package org.cru.godtools.tool.model

import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class FormTest : UsesResources() {
    @Test
    fun testParseForm() {
        val form = Form(Manifest(), getTestXmlParser("form.xml"))
        assertEquals(2, form.content.size)
        assertIs<Image>(form.content[0])
        assertIs<Text>(form.content[1])
    }

    @Test
    fun testParseParagraphIgnoredContent() {
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.MOBILE)
        val form = Form(Manifest(), getTestXmlParser("form_ignored_content.xml"))
        assertEquals(1, form.content.size)
        assertIs<Text>(form.content[0])
    }
}
