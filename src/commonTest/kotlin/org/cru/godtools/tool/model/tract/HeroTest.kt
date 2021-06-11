package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.DEFAULT_SUPPORTED_DEVICE_TYPES
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.DeviceType
import org.cru.godtools.tool.model.Image
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Paragraph
import org.cru.godtools.tool.model.Tabs
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class HeroTest : UsesResources("model/tract") {
    @BeforeTest
    fun setupConfig() {
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.ANDROID, DeviceType.MOBILE)
    }

    @AfterTest
    fun resetConfig() {
        ParserConfig.supportedDeviceTypes = DEFAULT_SUPPORTED_DEVICE_TYPES
    }

    @Test
    fun testParseHero() {
        val hero = Hero(Manifest(), getTestXmlParser("hero.xml"))
        assertEquals(1, hero.analyticsEvents.size)
        assertEquals("Heading", hero.heading!!.text)
        assertEquals(3, hero.content.size)
        assertIs<Image>(hero.content[0])
        assertIs<Paragraph>(hero.content[1])
        assertIs<Tabs>(hero.content[2])
    }

    @Test
    fun testParseHeroIgnoredContent() {
        val hero = Hero(Manifest(), getTestXmlParser("hero_ignored_content.xml"))
        assertEquals(2, hero.content.size)
        assertIs<Paragraph>(hero.content[0])
        assertIs<Tabs>(hero.content[1])
    }
}
