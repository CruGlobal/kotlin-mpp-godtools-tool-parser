package org.cru.godtools.expressions

import org.cru.godtools.tool.state.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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

    @Test
    fun testEvaluateAnd() {
        assertExpression("true && true", true)
        assertExpression("true && false", false)
        assertExpression("false && true", false)
        assertExpression("false && false", false)
        assertExpression("true && true && true", true)
        assertExpression("true && true && false", false)
        assertExpression("true && false && true", false)
    }

    @Test
    fun testEvaluateOr() {
        assertExpression("true || true", true)
        assertExpression("true || false", true)
        assertExpression("false || true", true)
        assertExpression("false || false", false)
    }

    private fun assertExpression(expr: String, expected: Boolean, state: State = this.state) {
        val compiled = assertNotNull(expr.toExpressionOrNull())
        assertEquals(expected, compiled.evaluate(state), "'$expr` evaluated incorrectly")
    }
}
