package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class AnalyticsEventTest : UsesResources() {
    @Test
    fun testParseAnalyticsEvent() {
        val event = AnalyticsEvent(Manifest(), getTestXmlParser("analytics_event.xml"))
        assertEquals("test", event.action)
        assertTrue(event.isForSystem(AnalyticsEvent.System.FIREBASE))
        AnalyticsEvent.System.values().filterNot { it == AnalyticsEvent.System.FIREBASE }.forEach {
            assertFalse(event.isForSystem(it))
        }
        assertTrue(event.isTriggerType(AnalyticsEvent.Trigger.DEFAULT))
        assertEquals(50, event.delay)
        assertEquals(1, event.attributes.size)
        assertContains(event.attributes, "attr")
        assertEquals("value", event.attributes["attr"])
    }

    @Test
    fun testParseAnalyticsEvents() {
        val events = getTestXmlParser("analytics_events.xml").parseAnalyticsEvents(Manifest())
        assertEquals(2, events.size)
        assertEquals("event1", events[0].action)
        assertEquals("event2", events[1].action)
    }
}
