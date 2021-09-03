package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

@RunOnAndroidWith(AndroidJUnit4::class)
class LinkTest : UsesResources() {
    @Test
    fun testParseLink() = runBlockingTest {
        val link = Link(Manifest(), getTestXmlParser("link.xml"))
        assertEquals("Test", link.text!!.text)
        assertEquals(2, link.events.size)
        assertEquals("event1", link.events[0].name)
        assertEquals("event2", link.events[1].name)
        assertEquals(1, link.analyticsEvents.size)
        assertEquals("test", assertIs<AnalyticsEvent>(link.analyticsEvents.single()).action)
    }

    @Test
    fun testLinkTextColor() {
        val parent = object : BaseModel(), Styles {
            override val primaryColor = TestColors.BLUE
        }

        assertEquals(parent.primaryColor, Link(parent).textColor)

        with(Link(parent) { Text(it) }) {
            assertNotEquals(parent.textColor, text!!.textColor)
            assertEquals(parent.primaryColor, text!!.textColor)
            assertEquals(TestColors.BLUE, text!!.textColor)
        }

        with(Link(parent) { Text(it, textColor = TestColors.GREEN) }) {
            assertNotEquals(parent.primaryColor, text!!.textColor)
            assertEquals(TestColors.GREEN, text!!.textColor)
        }
    }

    @Test
    fun testGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.DEFAULT)
        val selectedEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.SELECTED)
        val visibleEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.VISIBLE)
        val link = Link(analyticsEvents = listOf(defaultEvent, selectedEvent, visibleEvent))

        assertEquals(listOf(defaultEvent, selectedEvent), link.getAnalyticsEvents(AnalyticsEvent.Trigger.SELECTED))
        assertFailsWith(IllegalStateException::class) { link.getAnalyticsEvents(AnalyticsEvent.Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { link.getAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE) }
    }
}
