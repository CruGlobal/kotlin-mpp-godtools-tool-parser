package org.cru.godtools.shared.tool.parser.expressions

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail
import org.cru.godtools.shared.tool.parser.expressions.ExpressionContext
import org.cru.godtools.shared.tool.parser.expressions.SimpleExpressionContext

class ExpressionTest {
    private val ctx = SimpleExpressionContext()

    @Test
    fun testParseNullOrEmptyExpression() {
        assertNull((null as String?).toExpressionOrNull())
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
    fun testEquals() {
        val expr = "true".toExpressionOrNull()!!
        assertEquals(expr, expr)
        assertEquals(expr, "true".toExpressionOrNull())
        assertNotEquals(expr, "false".toExpressionOrNull())
        assertFalse(expr.equals(null))
        assertFalse(expr.equals("true"))
    }

    @Test
    fun testHashCode() {
        assertEquals("true".toExpressionOrNull()!!.hashCode(), "true".toExpressionOrNull()!!.hashCode())
    }

    @Test
    fun testEvaluateInvalidExpression() {
        try {
            assertNotNull("asdf".toExpressionOrNull()).evaluate(ctx)
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

        ctx.setVar("a", listOf("test"))
        assertExpression("a==\"test\"", true)

        ctx.setVar("a", listOf("other", "test", "something"))
        assertExpression("a==\"test\"", true)

        ctx.removeVarValue("a", "test")
        assertExpression("a==\"test\"", false)
    }

    @Test
    fun testEvaluateNotEquals() {
        assertExpression("a!=\"test\"", true)

        ctx.setVar("a", listOf("test"))
        assertExpression("a!=\"test\"", false)

        ctx.setVar("a", listOf("other", "test", "something"))
        assertExpression("a!=\"test\"", false)

        ctx.removeVarValue("a", "test")
        assertExpression("a!=\"test\"", true)
    }

    @Test
    fun testEvaluateIntCompare() {
        assertExpression("1 == 1", true)
        assertExpression("1 == 2", false)

        assertExpression("1 != 2", true)
        assertExpression("1 != 1", false)

        assertExpression("1 > 0", true)
        assertExpression("1 > 1", false)
        assertExpression("1 > 2", false)

        assertExpression("1 >= 0", true)
        assertExpression("1 >= 1", true)
        assertExpression("1 >= 2", false)

        assertExpression("1 < 0", false)
        assertExpression("1 < 1", false)
        assertExpression("1 < 2", true)

        assertExpression("1 <= 0", false)
        assertExpression("1 <= 1", true)
        assertExpression("1 <= 2", true)
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
        ctx.setVar("a", listOf("test"))
        assertExpression("isSet(a)", true)
        assertExpression("isSet(b)", false)
    }

    @Test
    fun testEvaluateFunctionValues() {
        ctx.setVar("a", listOf("test"))
        assertExpression("values(a) == 1", true)
        ctx.setVar("a", listOf("test", "test2"))
        assertExpression("values(a) == 2", true)
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

    @Test
    fun testVars() {
        assertEquals(setOf("a"), "a=='test'".toExpressionOrNull()!!.vars())
        assertEquals(setOf("a", "b"), "(true && (a=='' && isSet(b)))".toExpressionOrNull()!!.vars())
    }

    private fun assertExpression(expr: String, expected: Boolean, ctx: ExpressionContext = this.ctx) {
        val compiled = assertNotNull(expr.toExpressionOrNull())
        assertTrue(compiled.isValid(), "'$expr' should be a valid expression")
        assertEquals(expected, compiled.evaluate(ctx), "'$expr` evaluated incorrectly")
    }
}
