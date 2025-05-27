package org.cru.godtools.shared.renderer.state

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.cru.godtools.shared.common.model.toUriOrNull
import org.cru.godtools.shared.tool.parser.model.EventId

private const val KEY = "key"
private const val KEY2 = "key2"

class StateTest {
    private val state = State()

    // region Analytics Events Tracking
    @Test
    fun testAnalyticsEventsTracking() {
        assertEquals(0, state.getTriggeredAnalyticsEventsCount(KEY))
        assertEquals(0, state.getTriggeredAnalyticsEventsCount(KEY2))

        state.recordTriggeredAnalyticsEvent(KEY)
        assertEquals(1, state.getTriggeredAnalyticsEventsCount(KEY))

        state.recordTriggeredAnalyticsEvent(KEY)
        assertEquals(2, state.getTriggeredAnalyticsEventsCount(KEY))
        assertEquals(0, state.getTriggeredAnalyticsEventsCount(KEY2))

        state.recordTriggeredAnalyticsEvent(KEY2)
        assertEquals(1, state.getTriggeredAnalyticsEventsCount(KEY2))
    }
    // endregion Analytics Events Tracking

    // region State Vars
    @Test
    fun testGetVars() {
        assertTrue(state.getVar("missing").isEmpty())
        state.setVar("single", listOf("value"))
        assertEquals(listOf("value"), state.getVar("single"))
        state.setVar("multiple", listOf("a", "b", "c"))
        assertEquals(listOf("a", "b", "c"), state.getVar("multiple"))
    }

    @Test
    fun testVarsChangeFlow() = runTest {
        var i = 0
        state.varsChangeFlow(setOf(KEY, KEY2)) { i++ }.test {
            // initial value
            assertEquals(0, awaitItem())

            // update state for monitored key
            state.setVar(KEY, listOf("a"))
            assertEquals(1, awaitItem())

            // update state for other monitored key
            state.setVar(KEY2, listOf("a"))
            assertEquals(2, awaitItem())

            // update state for a different key
            state.setVar("other$KEY", listOf("a"))
            expectNoEvents()
        }
        assertEquals(3, i)
    }

    @Test
    fun testVarsChangeFlowChangeDuringStartup() = runTest {
        state.setVar(KEY, listOf("a"))
        state.varsChangeFlow(setOf(KEY)) {
            val value = state.getVar(KEY).single()
            // when emitting the initial value, update the key to check that changes during startup are handled
            if (value == "a") state.setVar(KEY, listOf("b"))
            value
        }.test {
            // initial value
            assertEquals("a", awaitItem())

            // final value
            assertEquals("b", awaitItem())
        }
        assertEquals("b", state.getVar(KEY).single())
    }

    @Test
    fun testVarsChangeFlowNoKeys() = runTest {
        var count = 0
        state.varsChangeFlow { count++ }.test {
            assertEquals(0, awaitItem())
            awaitComplete()
        }
        assertEquals(1, count)
    }

    @Test
    fun testAddVarValue() {
        assertTrue(state.getVar(KEY).isEmpty())

        state.addVarValue(KEY, "1")
        state.addVarValue(KEY, "2")
        assertEquals(listOf("1", "2"), state.getVar(KEY))

        state.addVarValue(KEY, "1")
        assertEquals(listOf("1", "2"), state.getVar(KEY))
    }

    @Test
    fun testRemoveVarValue() {
        state.setVar(KEY, listOf("1", "2", "3"))

        state.removeVarValue(KEY, "2")
        state.removeVarValue(KEY, "4")
        assertEquals(listOf("1", "3"), state.getVar(KEY))
    }
    // endregion State Vars

    // region Content Events
    @Test
    fun `triggerContentEvents - All Events emitted`() = runTest {
        val events = listOf(EventId(name = "event1"), EventId(name = "event2"))

        state.contentEvents.test {
            state.triggerContentEvents(events)
            assertEquals(events[0], awaitItem())
            assertEquals(events[1], awaitItem())
        }
    }

    @Test
    fun `triggerContentEvents - Resolve State EventIds`() = runTest {
        state.setVar(KEY, listOf("state_value1", "state_value2"))
        val events = listOf(
            EventId(name = "event1"),
            EventId(namespace = EventId.NAMESPACE_STATE, name = KEY),
            EventId(name = "event2"),
        )

        state.contentEvents.test {
            state.triggerContentEvents(events)
            assertEquals(EventId(name = "event1"), awaitItem())
            assertEquals(EventId(name = "state_value1"), awaitItem())
            assertEquals(EventId(name = "state_value2"), awaitItem())
            assertEquals(EventId(name = "event2"), awaitItem())
        }
    }

    @Test
    fun `triggerContentEvents - Ensure first event can launch a new subscriber for subsequent events`() = runTest {
        var order = 0
        val mutex = Mutex(true)
        val events = listOf(EventId(name = "event1"), EventId(name = "event2"))

        // first subscriber
        launch(start = CoroutineStart.UNDISPATCHED) {
            // this subscriber should only collect the first event
            state.contentEvents.take(1).collect {
                assertEquals(2, order++)
                assertEquals(events[0], it)
                // we unlock the mutex and yield to start the second subscriber
                mutex.unlock()
                yield()
            }
        }
        // second subscriber
        launch(start = CoroutineStart.UNDISPATCHED) {
            // this subscriber suspends until started by the first subscriber
            assertEquals(0, order++)
            mutex.withLock {
                // start the second subscriber
                assertEquals(3, order++)
                state.contentEvents.test {
                    // this should only collect remaining events
                    assertEquals(events[1], awaitItem())
                }
            }
        }

        // trigger the events
        assertEquals(1, order++)
        state.triggerContentEvents(events)
    }
    // endregion Content Events

    // region Events
    @Test
    fun `triggerOpenUrlEvent - Event emitted`() = runTest {
        val url = "https://example.com".toUriOrNull()!!

        state.events.test {
            state.triggerOpenUrlEvent(url)
            assertEquals(State.Event.OpenUrl(url), awaitItem())
        }
    }
    // endregion Events
}
