package org.cru.godtools.tool.state

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.cru.godtools.tool.internal.coroutines.receive
import org.cru.godtools.tool.internal.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val KEY = "key"

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
    fun testChangeFlow() = runBlockingTest {
        val channel = Channel<Unit>()
        val flow = state.changeFlow(KEY)
            .onEach { channel.send(it) }
            .launchIn(this)

        // initial value
        assertEquals(Unit, channel.receive(500))

        // update state for monitored key
        state[KEY] = "a"
        assertEquals(Unit, channel.receive(500))

        // update state for a different key
        state["other$KEY"] = "a"
        delay(100)
        assertTrue(channel.isEmpty)

        // shut down flow
        flow.cancel()
    }

    @Test
    fun testAddValue() {
        assertTrue(state.getAll(KEY).isEmpty())

        state.addValue(KEY, "1")
        state.addValue(KEY, "2")
        assertEquals(listOf("1", "2"), state.getAll(KEY))

        state.addValue(KEY, "1")
        assertEquals(listOf("1", "2"), state.getAll(KEY))
    }

    @Test
    fun testRemoveValue() {
        state[KEY] = listOf("1", "2", "3")

        state.removeValue(KEY, "2")
        state.removeValue(KEY, "4")
        assertEquals(listOf("1", "3"), state.getAll(KEY))
    }
}
