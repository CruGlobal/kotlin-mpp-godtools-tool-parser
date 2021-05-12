package org.cru.godtools.tool.state

import org.cru.godtools.tool.model.EVENT_NAMESPACE_STATE
import org.cru.godtools.tool.model.EventId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StateTest {
    private val state = State()

    @Test
    fun testGet() {
        assertNull(state["missing"])
        state["single"] = "value"
        assertEquals("value", state["single"])
        state["multiple"] = listOf("a", "b", "c")
        assertEquals("a", state["multiple"])
    }

    @Test
    fun testGetAll() {
        assertTrue(state.getAll("missing").isEmpty())
        state["single"] = "value"
        assertEquals(listOf("value"), state.getAll("single"))
        state["multiple"] = listOf("a", "b", "c")
        assertEquals(listOf("a", "b", "c"), state.getAll("multiple"))
    }

    @Test
    fun testResolveEventId() {
        state["selectorState"] = listOf("a", "b")
        val events = state.resolveEventId(EventId(EVENT_NAMESPACE_STATE, "selectorState"))
        assertEquals(2, events.size)
        assertEquals(EventId(name = "a"), events[0])
        assertEquals(EventId(name = "b"), events[1])
    }

    @Test
    fun testResolveEventIdUnset() {
        assertTrue(state.resolveEventId(EventId(EVENT_NAMESPACE_STATE, "missing")).isEmpty())
    }

    @Test
    fun testResolveEventIdDifferentNamespace() {
        val event = EventId(name = "event")
        state["event"] = listOf("a", "b")
        assertEquals(event, state.resolveEventId(event).single())
    }
}
