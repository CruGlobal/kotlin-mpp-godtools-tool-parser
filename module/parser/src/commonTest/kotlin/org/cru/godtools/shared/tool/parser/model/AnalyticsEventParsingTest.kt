package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources

@RunOnAndroidWith(AndroidJUnit4::class)
class AnalyticsEventParsingTest : UsesResources() {
    // region Parsing
    @Test
    fun testParseAnalyticsEventDefaults() = runTest {
        val event = getTestXmlParser("analytics_event_defaults.xml").parseAnalyticsEvent()
        assertEquals("", event.id)
        assertEquals("", event.action)
        assertTrue(event.isForSystem(AnalyticsEvent.System.USER))
        AnalyticsEvent.System.entries.filterNot { it == AnalyticsEvent.System.USER }.forEach {
            assertFalse(event.isForSystem(it))
        }
        assertTrue(event.isTriggerType(AnalyticsEvent.Trigger.DEFAULT))
        assertEquals(0, event.delay)
        assertEquals(0, event.attributes.size)
        assertNull(event.limit)
    }

    @Test
    fun testParseAnalyticsEvent() = runTest {
        val event = getTestXmlParser("analytics_event.xml").parseAnalyticsEvent()
        assertEquals("id", event.id)
        assertEquals("test", event.action)
        assertTrue(event.isForSystem(AnalyticsEvent.System.FIREBASE))
        AnalyticsEvent.System.entries.filterNot { it == AnalyticsEvent.System.FIREBASE }.forEach {
            assertFalse(event.isForSystem(it))
        }
        assertTrue(event.isTriggerType(AnalyticsEvent.Trigger.VISIBLE))
        assertEquals(50, event.delay)
        assertEquals(10, event.limit)
        assertEquals(1, event.attributes.size)
        assertContains(event.attributes, "attr")
        assertEquals("value", event.attributes["attr"])
    }

    @Test
    fun testParseAnalyticsEvents() = runTest {
        val events = getTestXmlParser("analytics_events.xml").parseAnalyticsEvents()
        assertEquals(2, events.size)
        assertEquals("event1", events[0].id)
        assertEquals("event1", events[0].action)
        assertEquals("event2-id", events[1].id)
        assertEquals("event2", events[1].action)
    }

    @Test
    fun testParseAnalyticsEventSystem() {
        assertEquals(setOf(AnalyticsEvent.System.FACEBOOK), "facebook".toAnalyticsEventSystems())
        assertEquals(setOf(AnalyticsEvent.System.FIREBASE), "firebase".toAnalyticsEventSystems())
        assertEquals(setOf(AnalyticsEvent.System.USER), "user".toAnalyticsEventSystems())
        assertEquals(0, "jkalsdf".toAnalyticsEventSystems().size)
    }

    @Test
    fun testParseAnalyticsEventTrigger() {
        assertEquals(AnalyticsEvent.Trigger.VISIBLE, "visible".toAnalyticsEventTrigger())
        assertEquals(AnalyticsEvent.Trigger.HIDDEN, "hidden".toAnalyticsEventTrigger())
        assertEquals(AnalyticsEvent.Trigger.UNKNOWN, "jkalsdf".toAnalyticsEventTrigger())
    }
    // endregion Parsing
}
