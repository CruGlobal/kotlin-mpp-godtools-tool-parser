package org.cru.godtools.tool.state

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.cru.godtools.tool.internal.receive
import org.cru.godtools.tool.internal.runBlockingTest
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
    fun testChangeFlow() = runBlockingTest {
        val channel = Channel<String>()
        val flow = state.changeFlow("test")
            .onEach { channel.send(it) }
            .launchIn(this)

        // initial value
        assertEquals("test", channel.receive(100))

        // update state for monitored key
        state["test"] = "a"
        assertEquals("test", channel.receive(100))

        // update state for a different key
        state["other"] = "a"
        delay(100)
        assertTrue(channel.isEmpty)

        // shut down flow
        flow.cancel()
    }
}
