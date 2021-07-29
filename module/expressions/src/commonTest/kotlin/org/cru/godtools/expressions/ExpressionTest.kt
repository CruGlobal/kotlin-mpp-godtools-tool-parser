package org.cru.godtools.expressions

import org.cru.godtools.tool.state.State
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpressionTest {
    private val state = State()

    @Test
    fun testIsValid() {
        listOf("true", "false", "a==\"test\"").forEach {
            assertTrue(it.toExpressionOrNull()!!.isValid(), "'$it' should be a valid expression")
        }
    }

    @Test
    fun testIsValidInvalid() {
        listOf("asdf").forEach {
            assertFalse(it.toExpressionOrNull()!!.isValid(), "'$it' should be an invalid expression")
        }
    }

    @Test
    fun testEvaluateBoolean() {
        assertTrue("true".toExpressionOrNull()!!.evaluate(state))
        assertFalse("false".toExpressionOrNull()!!.evaluate(state))
    }

    @Test
    fun testEvaluateNot() {
        assertTrue("!false".toExpressionOrNull()!!.evaluate(state))
        assertFalse("!true".toExpressionOrNull()!!.evaluate(state))
    }

    @Test
    fun testEvaluateEquals() {
        val expr = "a==\"test\"".toExpressionOrNull()!!
        assertFalse(expr.evaluate(state))

        state["a"] = "test"
        assertTrue(expr.evaluate(state))

        state["a"] = listOf("other", "test", "something")
        assertTrue(expr.evaluate(state))

        state.removeValue("a", "test")
        assertFalse(expr.evaluate(state))
    }
}
