package org.cru.godtools.expressions

import org.cru.godtools.tool.state.State
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class ExpressionTest {
    private val state = State()

    @Test
    fun testParseEmptyExpression() {
        assertNull("".toExpressionOrNull())
        assertNull("   ".toExpressionOrNull())
    }

    @Test
    fun testIsValid() {
        listOf("true&&false", "true   && false", "     true    ", "isSet(var)", "isSet(   var   )").forEach {
            assertTrue(it.toExpressionOrNull()!!.isValid(), "'$it' should be a valid expression")
        }
    }

    @Test
    fun testIsValidInvalid() {
        listOf(
            "asdf",
            "true asdf",
            "true AND false",
            "1",
            "()",
            "isSet(a==\"b\")"
        ).forEach { assertFalse(it.toExpressionOrNull()!!.isValid(), "'$it' should be an invalid expression") }
    }

    @Test
    fun testEvaluateInvalidExpression() {
        try {
            assertNotNull("asdf".toExpressionOrNull()).evaluate(state)
            fail("Invalid expressions should throw an IllegalStateException when evaluated")
        } catch (_: IllegalStateException) {
        }
    }

    @Test
    fun testEvaluateBoolean() {
        assertExpression("true", true)
        assertExpression("false", false)
    }

    @Test
    fun testEvaluateNot() {
        assertExpression("!false", true)
        assertExpression("!true", false)
        assertExpression("!!true", true)
    }

    @Test
    fun testEvaluateEquals() {
        assertExpression("a==\"test\"", false)

        state["a"] = "test"
        assertExpression("a==\"test\"", true)

        state["a"] = listOf("other", "test", "something")
        assertExpression("a==\"test\"", true)

        state.removeValue("a", "test")
        assertExpression("a==\"test\"", false)
    }

    @Test
    fun testEvaluateParenthesis() {
        assertExpression("(true)", true)
        assertExpression("(false)", false)
        assertExpression("(false && false) || true", true)
        assertExpression("false && ((false) || true)", false)
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

    @Test
    fun testEvaluateFunctionIsSet() {
        state["a"] = "test"
        assertExpression("isSet(a)", true)
        assertExpression("isSet(b)", false)
    }

    @Test
    @Ignore // antlr-kotlin doesn't correctly honor operator precedence at this time
    fun testEvaluateOperatorPrecedence() {
        // && has higher precedence than ||
        assertExpression("false && false || true", true)
        assertExpression("true || false && false", true)

        // ! has higher precedence than &&
        assertExpression("false && !false", false)
        assertExpression("!false && false", false)

        // ! has higher precedence than ||
        assertExpression("!true || true", true)
        assertExpression("true || !true", true)
    }

    private fun assertExpression(expr: String, expected: Boolean, state: State = this.state) {
        val compiled = assertNotNull(expr.toExpressionOrNull())
        assertTrue(compiled.isValid(), "'$expr' should be a valid expression")
        assertEquals(expected, compiled.evaluate(state), "'$expr` evaluated incorrectly")
    }
}
