package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.System.Companion.toAnalyticsSystems
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger.Companion.toTrigger
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsEventTest : UsesResources() {
    @Test
    fun testParseAnalyticsEventDefaults() = runTest {
        val event = AnalyticsEvent(Manifest(), getTestXmlParser("analytics_event_defaults.xml"))
        assertNull(event.action)
        assertTrue(event.isForSystem(AnalyticsEvent.System.APPSFLYER))
        AnalyticsEvent.System.values().filterNot { it == AnalyticsEvent.System.APPSFLYER }.forEach {
            assertFalse(event.isForSystem(it))
        }
        assertTrue(event.isTriggerType(AnalyticsEvent.Trigger.DEFAULT))
        assertEquals(0, event.delay)
        assertEquals(0, event.attributes.size)
    }

    @Test
    fun testParseAnalyticsEvent() = runTest {
        val event = AnalyticsEvent(Manifest(), getTestXmlParser("analytics_event.xml"))
        assertEquals("test", event.action)
        assertTrue(event.isForSystem(AnalyticsEvent.System.FIREBASE))
        AnalyticsEvent.System.values().filterNot { it == AnalyticsEvent.System.FIREBASE }.forEach {
            assertFalse(event.isForSystem(it))
        }
        assertTrue(event.isTriggerType(AnalyticsEvent.Trigger.VISIBLE))
        assertEquals(50, event.delay)
        assertEquals(1, event.attributes.size)
        assertContains(event.attributes, "attr")
        assertEquals("value", event.attributes["attr"])
    }

    @Test
    fun testParseAnalyticsEvents() = runTest {
        val events = getTestXmlParser("analytics_events.xml").parseAnalyticsEvents(Manifest())
        assertEquals(2, events.size)
        assertEquals("event1", events[0].action)
        assertEquals("event2", events[1].action)
    }

    @Test
    fun testParseAnalyticsEventSystem() {
        assertEquals(setOf(AnalyticsEvent.System.ADOBE), "adobe".toAnalyticsSystems())
        assertEquals(setOf(AnalyticsEvent.System.APPSFLYER), "appsflyer".toAnalyticsSystems())
        assertEquals(setOf(AnalyticsEvent.System.FACEBOOK), "facebook".toAnalyticsSystems())
        assertEquals(setOf(AnalyticsEvent.System.FIREBASE), "firebase".toAnalyticsSystems())
        assertEquals(setOf(AnalyticsEvent.System.SNOWPLOW), "snowplow".toAnalyticsSystems())
        assertEquals(0, "jkalsdf".toAnalyticsSystems().size)
    }

    @Test
    fun testParseAnalyticsEventTrigger() {
        assertEquals(AnalyticsEvent.Trigger.SELECTED, "selected".toTrigger())
        assertEquals(AnalyticsEvent.Trigger.VISIBLE, "visible".toTrigger())
        assertEquals(AnalyticsEvent.Trigger.HIDDEN, "hidden".toTrigger())
        assertEquals(AnalyticsEvent.Trigger.UNKNOWN, "jkalsdf".toTrigger())
    }
}
