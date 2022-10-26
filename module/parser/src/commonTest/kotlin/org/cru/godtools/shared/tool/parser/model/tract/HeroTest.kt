package org.cru.godtools.shared.tool.parser.model.tract

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.internal.AndroidJUnit4
import org.cru.godtools.shared.tool.parser.internal.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.DeviceType
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.withDeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class HeroTest : UsesResources("model/tract") {
    @Test
    fun testParseHero() = runTest {
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
    fun testParseHeroIgnoredContent() = runTest {
        val config = ParserConfig().withDeviceType(DeviceType.ANDROID)
        val page = TractPage(Manifest(config = config), null, getTestXmlParser("hero_ignored_content.xml"))
        with(assertNotNull(page.hero)) {
            assertEquals(2, content.size)
            assertIs<Paragraph>(content[0])
            assertIs<Tabs>(content[1])
        }
    }

    @Test
    fun testAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val hiddenEvent = AnalyticsEvent(trigger = Trigger.HIDDEN)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val hero = Hero(analyticsEvents = listOf(defaultEvent, visibleEvent, hiddenEvent, clickedEvent))

        assertEquals(listOf(defaultEvent, visibleEvent), hero.getAnalyticsEvents(Trigger.VISIBLE))
        assertEquals(listOf(hiddenEvent), hero.getAnalyticsEvents(Trigger.HIDDEN))
        assertFailsWith(IllegalStateException::class) { hero.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { hero.getAnalyticsEvents(Trigger.CLICKED) }
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
