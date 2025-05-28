package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.cru.godtools.shared.renderer.state.State

class AnalyticsEventStateTest {
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
