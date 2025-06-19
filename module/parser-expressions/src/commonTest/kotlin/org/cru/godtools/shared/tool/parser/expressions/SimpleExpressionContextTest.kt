package org.cru.godtools.shared.tool.parser.expressions

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SimpleExpressionContextTest {
    companion object {
        private const val KEY = "key"
        private const val KEY2 = "key2"
    }

    val state = SimpleExpressionContext()

    // region Vars
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
    // endregion Vars
}
