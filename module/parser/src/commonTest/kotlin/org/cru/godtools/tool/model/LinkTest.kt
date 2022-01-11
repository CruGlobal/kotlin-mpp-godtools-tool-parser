package org.cru.godtools.tool.model

import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.model.EventId.Companion.FOLLOWUP
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class LinkTest : UsesResources() {
    @Test
    fun testParseLink() = runTest {
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
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val selectedEvent = AnalyticsEvent(trigger = Trigger.SELECTED)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val link = Link(analyticsEvents = listOf(defaultEvent, clickedEvent, selectedEvent, visibleEvent))

        assertEquals(listOf(defaultEvent, clickedEvent), link.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { link.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { link.getAnalyticsEvents(Trigger.SELECTED) }
        assertFailsWith(IllegalStateException::class) { link.getAnalyticsEvents(Trigger.VISIBLE) }
    }

    @Test
    fun testIsIgnoredClickableState() {
        with(Link()) {
            assertFalse(isClickable)
            assertTrue(testIsIgnored)
        }

        with(Link(events = listOf(FOLLOWUP))) {
            assertTrue(isClickable)
            assertFalse(testIsIgnored)
        }

        with(Link(url = TEST_URL)) {
            assertTrue(isClickable)
            assertFalse(testIsIgnored)
        }
    }
}
