package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.DeviceType
import org.cru.godtools.tool.model.Image
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Paragraph
import org.cru.godtools.tool.model.Tabs
import org.cru.godtools.tool.model.TestColors
import org.cru.godtools.tool.model.Text
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@RunOnAndroidWith(AndroidJUnit4::class)
class HeroTest : UsesResources("model/tract") {
    @BeforeTest
    fun setupConfig() {
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.ANDROID, DeviceType.MOBILE)
    }

    @Test
    fun testParseHero() = runBlockingTest {
        val hero = assertNotNull(TractPage(Manifest(), null, getTestXmlParser("hero.xml")).hero)
        assertEquals(1, hero.analyticsEvents.size)
        assertEquals("Heading", hero.heading!!.text)
        assertEquals(hero.stylesParent!!.primaryColor, hero.heading!!.textColor)
        assertNotEquals(hero.stylesParent!!.textColor, hero.heading!!.textColor)
        assertEquals(3, hero.content.size)
        assertIs<Image>(hero.content[0])
        assertIs<Paragraph>(hero.content[1])
        assertIs<Tabs>(hero.content[2])
    }

    @Test
    fun testParseHeroIgnoredContent() = runBlockingTest {
        val hero = assertNotNull(TractPage(Manifest(), null, getTestXmlParser("hero_ignored_content.xml")).hero)
        assertEquals(2, hero.content.size)
        assertIs<Paragraph>(hero.content[0])
        assertIs<Tabs>(hero.content[1])
    }

    @Test
    fun testHeadingTextColor() {
        val page = TractPage(primaryColor = TestColors.BLUE)
        with(Hero(page, heading = { Text(it) })) {
            assertEquals(page.primaryColor, heading!!.textColor)
        }

        with(Hero(page, heading = { Text(it, textColor = TestColors.GREEN) })) {
            assertEquals(TestColors.GREEN, heading!!.textColor)
            assertNotEquals(page.primaryColor, heading!!.textColor)
        }
    }
}
