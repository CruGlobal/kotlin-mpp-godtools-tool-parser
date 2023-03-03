package org.cru.godtools.shared.tool.state

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val KEY = "key"
private const val KEY2 = "key2"

@OptIn(ExperimentalCoroutinesApi::class)
class StateTest {
    private val state = State()

    @Test
    fun testGetAll() {
        assertTrue(state.getAll("missing").isEmpty())
        state.setVar("single", listOf("value"))
        assertEquals(listOf("value"), state.getAll("single"))
        state.setVar("multiple", listOf("a", "b", "c"))
        assertEquals(listOf("a", "b", "c"), state.getAll("multiple"))
    }

    @Test
    fun testChangeFlow() = runTest {
        var i = 0
        state.varsChangeFlow(KEY, KEY2) { i++ }.test {
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
    fun testChangeFlowNoKeys() = runTest {
        var count = 0
        state.varsChangeFlow { count++ }.test {
            assertEquals(0, awaitItem())
            awaitComplete()
        }
        assertEquals(1, count)
    }

    @Test
    fun testAddValue() {
        assertTrue(state.getAll(KEY).isEmpty())

        state.addVarValue(KEY, "1")
        state.addVarValue(KEY, "2")
        assertEquals(listOf("1", "2"), state.getAll(KEY))

        state.addVarValue(KEY, "1")
        assertEquals(listOf("1", "2"), state.getAll(KEY))
    }

    @Test
    fun testRemoveValue() {
        state.setVar(KEY, listOf("1", "2", "3"))

        state.removeVarValue(KEY, "2")
        state.removeVarValue(KEY, "4")
        assertEquals(listOf("1", "3"), state.getAll(KEY))
    }
}
