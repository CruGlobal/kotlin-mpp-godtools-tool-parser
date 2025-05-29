package org.cru.godtools.shared.renderer.content.extensions

import app.cash.turbine.test
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents

@OptIn(ExperimentalCoroutinesApi::class)
class HasAnalyticsEventsStateTest {
    private val event1 = AnalyticsEvent(id = "event1")
    private val event2 = AnalyticsEvent(id = "event2")
    private val event3 = AnalyticsEvent(id = "event3")
    private val event4 = AnalyticsEvent(id = "event4")
    private val eventDelayed = AnalyticsEvent(id = "delayed", delay = 1)

    private val state = State()

    private val testScope = TestScope()

    @BeforeTest
    fun setup() {
        state.setTestCoroutineScope(testScope)
    }

    @Test
    fun `triggerAnalyticsEvents - Triggers matching events`() = testScope.runTest {
        val model = object : HasAnalyticsEvents {
            override fun getAnalyticsEvents(type: AnalyticsEvent.Trigger) = when (type) {
                AnalyticsEvent.Trigger.CLICKED -> listOf(event1, event2)
                else -> listOf(event3, event4)
            }
        }

        state.events.test {
            model.triggerAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED, state, this@runTest)
            assertEquals(State.Event.AnalyticsEventTriggered(event1), awaitItem())
            assertEquals(State.Event.AnalyticsEventTriggered(event2), awaitItem())
            advanceUntilIdle()
            expectNoEvents()

            model.triggerAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE, state, this@runTest)
            assertEquals(State.Event.AnalyticsEventTriggered(event3), awaitItem())
            assertEquals(State.Event.AnalyticsEventTriggered(event4), awaitItem())
        }
    }

    @Test
    fun `triggerAnalyticsEvents - Delayed Events`() = testScope.runTest {
        val model = object : HasAnalyticsEvents {
            override fun getAnalyticsEvents(type: AnalyticsEvent.Trigger) = listOf(eventDelayed, event1)
        }

        state.events.test {
            launch { model.triggerAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED, state, this@runTest) }
            assertEquals(State.Event.AnalyticsEventTriggered(event1), awaitItem())

            advanceTimeBy(1_000)
            expectNoEvents()

            runCurrent()
            assertEquals(State.Event.AnalyticsEventTriggered(eventDelayed), expectMostRecentItem())
        }
    }

    @Test
    fun `triggerAnalyticsEvents - Delayed Events - Canceled`() = testScope.runTest {
        val model = object : HasAnalyticsEvents {
            override fun getAnalyticsEvents(type: AnalyticsEvent.Trigger) = listOf(eventDelayed, event1)
        }

        val job = launch(start = CoroutineStart.LAZY) {
            model.triggerAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED, state, this)
        }

        state.events.test {
            job.start()
            assertEquals(State.Event.AnalyticsEventTriggered(event1), awaitItem())

            advanceTimeBy(999)
            job.cancel()

            advanceUntilIdle()
        }
    }
}
