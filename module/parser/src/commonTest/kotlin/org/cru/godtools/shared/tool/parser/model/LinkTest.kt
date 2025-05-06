package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.EventId.Companion.FOLLOWUP

@RunOnAndroidWith(AndroidJUnit4::class)
class LinkTest : UsesResources() {
    @Test
    fun testParseLink() = runTest {
        val link = Link(Manifest(), getTestXmlParser("link.xml"))
        assertEquals("Test", link.text.text)
        assertEquals(2, link.events.size)
        assertEquals("event1", link.events[0].name)
        assertEquals("event2", link.events[1].name)
        assertEquals(1, link.analyticsEvents.size)
        assertEquals("test", assertIs<AnalyticsEvent>(link.analyticsEvents.single()).action)
    }

    @Test
    fun testLinkTextColor() {
        val parent = object : BaseModel(), Styles {
            override val primaryColor = TestColors.random().toPlatformColor()
        }

        assertEquals(parent.primaryColor.toRGB(), Link(parent).text.textColor)

        with(Link(parent) { Text(it) }) {
            assertNotEquals(parent.textColor, text.textColor)
            assertEquals(parent.primaryColor.toRGB(), text.textColor)
        }

        with(Link(parent) { Text(it, textColor = TestColors.GREEN) }) {
            assertNotEquals(parent.primaryColor.toRGB(), text.textColor)
            assertEquals(TestColors.GREEN, text.textColor)
        }
    }

    @Test
    fun testGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val link = Link(analyticsEvents = listOf(defaultEvent, clickedEvent, visibleEvent))

        assertEquals(listOf(defaultEvent, clickedEvent), link.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { link.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { link.getAnalyticsEvents(Trigger.VISIBLE) }
    }

    @Test
    fun testIsIgnoredClickableState() {
        with(Link()) {
            assertFalse(isClickable)
            assertTrue(isIgnored)
        }

        with(Link(events = listOf(FOLLOWUP))) {
            assertTrue(isClickable)
            assertFalse(isIgnored)
        }

        with(Link(url = TEST_URL)) {
            assertTrue(isClickable)
            assertFalse(isIgnored)
        }
    }
}
