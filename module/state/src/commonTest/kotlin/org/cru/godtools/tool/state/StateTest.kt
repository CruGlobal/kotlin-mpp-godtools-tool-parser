package org.cru.godtools.tool.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
private const val KEY2 = "key2"

@OptIn(ExperimentalCoroutinesApi::class)
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
        var i = 0
        val channel = Channel<Int>()
        val flow = state.changeFlow(KEY, KEY2) { i++ }
            .onEach { channel.send(it) }
            .launchIn(this)

        // initial value
        assertEquals(0, channel.receive(500))

        // update state for monitored key
        state[KEY] = "a"
        assertEquals(1, channel.receive(500))

        // update state for other monitored key
        state[KEY2] = "a"
        assertEquals(2, channel.receive(500))

        // update state for a different key
        state["other$KEY"] = "a"
        delay(100)
        assertTrue(channel.isEmpty)

        // shut down flow
        flow.cancel()
        assertEquals(3, i)
    }

    @Test
    fun testChangeFlowNoKeys() = runBlockingTest {
        var i = 0
        val channel = Channel<Int>()
        val flow = state.changeFlow { i++ }
            .onEach { channel.send(it) }
            .launchIn(this)

        // initial value
        assertEquals(0, channel.receive(500))

        // update state for multiple keys, should never emit a new value
        for (i in 1..10) {
            state["$KEY$i"] = "a"
            delay(100)
            assertTrue(channel.isEmpty)
        }

        // shut down flow
        flow.cancel()
        assertEquals(1, i)
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
