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
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.System.Companion.toAnalyticsSystems
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger.Companion.toTrigger

@RunOnAndroidWith(AndroidJUnit4::class)
class AnalyticsEventTest : UsesResources() {
    // region Parsing
    @Test
    fun testParseAnalyticsEventDefaults() = runTest {
        val event = AnalyticsEvent(getTestXmlParser("analytics_event_defaults.xml"))
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
        val event = AnalyticsEvent(getTestXmlParser("analytics_event.xml"))
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
        assertEquals(setOf(AnalyticsEvent.System.FACEBOOK), "facebook".toAnalyticsSystems())
        assertEquals(setOf(AnalyticsEvent.System.FIREBASE), "firebase".toAnalyticsSystems())
        assertEquals(setOf(AnalyticsEvent.System.USER), "user".toAnalyticsSystems())
        assertEquals(0, "jkalsdf".toAnalyticsSystems().size)
    }

    @Test
    fun testParseAnalyticsEventTrigger() {
        assertEquals(AnalyticsEvent.Trigger.VISIBLE, "visible".toTrigger())
        assertEquals(AnalyticsEvent.Trigger.HIDDEN, "hidden".toTrigger())
        assertEquals(AnalyticsEvent.Trigger.UNKNOWN, "jkalsdf".toTrigger())
    }
    // endregion Parsing

    // region Property: id
    @Test
    fun testIdFallback() {
        assertEquals("action", AnalyticsEvent(id = null, action = "action").id)
        assertEquals("id", AnalyticsEvent(id = "id", action = "action").id)
    }
    // endregion Property: id

    // region Record Triggered Events
    private val state = State()

    @Test
    fun testShouldTrigger() {
        val event = AnalyticsEvent(
            id = "id",
            limit = 2
        )
        assertTrue(event.shouldTrigger(state))

        event.recordTriggered(state)
        assertTrue(event.shouldTrigger(state))

        event.recordTriggered(state)
        assertFalse(event.shouldTrigger(state))
    }

    @Test
    fun testShouldTriggerNoLimit() {
        val event = AnalyticsEvent(id = "id")
        assertTrue(event.shouldTrigger(state))

        repeat(50) { event.recordTriggered(state) }
        assertTrue(event.shouldTrigger(state))
    }
    // endregion Record Triggered Events
}
