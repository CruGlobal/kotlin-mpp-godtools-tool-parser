package org.cru.godtools.shared.tool.parser.model

import kotlin.collections.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.cru.godtools.shared.renderer.state.State

class EventIdStateTest {
    // region resolve(State)
    @Test
    fun testResolve() {
        val state = State()
        state.setVar("selectorState", listOf("a", "b"))
        val events = EventId(EventId.NAMESPACE_STATE, "selectorState").resolve(state)
        assertEquals(2, events.size)
        assertEquals(EventId(name = "a"), events[0])
        assertEquals(EventId(name = "b"), events[1])
    }

    @Test
    fun testResolveUnset() {
        assertTrue(EventId(EventId.NAMESPACE_STATE, "missing").resolve(State()).isEmpty())
    }

    @Test
    fun testResolveDifferentNamespace() {
        val state = State()
        val event = EventId(name = "event")
        state.setVar("event", listOf("a", "b"))
        assertEquals(event, event.resolve(state).single())
    }
    // endregion resolve(State)
}
