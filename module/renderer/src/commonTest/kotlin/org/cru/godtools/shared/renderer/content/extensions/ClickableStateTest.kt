package org.cru.godtools.shared.renderer.content.extensions

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.TestConstants
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Clickable
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents

@RunOnAndroidWith(AndroidJUnit4::class)
class ClickableStateTest {
    private val analyticsEvent = AnalyticsEvent()
    private val event1 = EventId(name = "event1")
    private val event2 = EventId(name = "event2")

    private val state = State()

    private val testScope = TestScope()

    @BeforeTest
    fun setup() {
        state.setTestCoroutineScope(testScope)
    }

    @Test
    fun `handleClickable - Trigger Content Event`() = testScope.runTest {
        val model = object : Clickable {
            override val events = listOf(event1, event2)
            override val url = null
        }

        state.contentEvents.test {
            model.handleClickable(state, this)
            assertEquals(event1, awaitItem())
            assertEquals(event2, awaitItem())
        }
    }

    @Test
    fun `handleClickable - Trigger URL Event`() = testScope.runTest {
        val model = object : Clickable {
            override val events = emptyList<EventId>()
            override val url = TestConstants.TEST_URL
        }

        state.events.filterIsInstance<State.Event.OpenUrl>().test {
            model.handleClickable(state, this)
            assertEquals(TestConstants.TEST_URL, awaitItem().url)
        }
    }

    @Test
    fun `handleClickable - Triggers Clicked AnalyticsEvent`() = testScope.runTest {
        val model = object : Clickable, HasAnalyticsEvents {
            override val events = emptyList<EventId>()
            override val url = null
            override fun getAnalyticsEvents(type: AnalyticsEvent.Trigger) = when (type) {
                AnalyticsEvent.Trigger.CLICKED -> listOf(analyticsEvent)
                else -> emptyList()
            }
        }

        state.events.filterIsInstance<State.Event.AnalyticsEventTriggered>().test {
            model.handleClickable(state, this)
            assertEquals(analyticsEvent, awaitItem().event)
        }
    }

    @Test
    fun `handleClickable - Trigger Everything`() = testScope.runTest {
        val model = object : Clickable, HasAnalyticsEvents {
            override val events = listOf(event1, event2)
            override val url = TestConstants.TEST_URL
            override fun getAnalyticsEvents(type: AnalyticsEvent.Trigger) = when (type) {
                AnalyticsEvent.Trigger.CLICKED -> listOf(analyticsEvent)
                else -> emptyList()
            }
        }

        turbineScope {
            val analyticsEvents = state.events.filterIsInstance<State.Event.AnalyticsEventTriggered>().testIn(this)
            val contentEvents = state.contentEvents.testIn(this)
            val urlEvents = state.events.filterIsInstance<State.Event.OpenUrl>().testIn(this)

            model.handleClickable(state, this@runTest)
            assertEquals(analyticsEvent, analyticsEvents.awaitItem().event)
            assertEquals(event1, contentEvents.awaitItem())
            assertEquals(event2, contentEvents.awaitItem())
            assertEquals(TestConstants.TEST_URL, urlEvents.awaitItem().url)

            analyticsEvents.cancel()
            contentEvents.cancel()
            urlEvents.cancel()
        }
    }
}
