package org.cru.godtools.tool.model

import org.cru.godtools.tool.DEFAULT_SUPPORTED_DEVICE_TYPES
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class FallbackTest : UsesResources {
    override val resourcesDir = "model"

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
//
//    @Test
//    fun testParseParagraphFallback() {
//        val fallback = Fallback(Manifest(), getXmlParserForResource("fallback_paragraph.xml"))
//        assertThat(
//            fallback.content,
//            contains(instanceOf(Text::class.java), instanceOf(Text::class.java), instanceOf(Text::class.java))
//        )
//    }
//
//    @Test(expected = XmlPullParserException::class)
//    fun testParseParagraphFallbackInvalid() {
//        Fallback(Manifest(), getXmlParserForResource("paragraph.xml"))
//    }
}
