package org.cru.godtools.shared.tool.parser.expressions.grammar

import org.antlr.v4.kotlinruntime.Token
import org.cru.godtools.shared.tool.parser.expressions.ExpressionContext
import org.cru.godtools.shared.tool.parser.expressions.grammar.generated.StateExpressionBaseVisitor
import org.cru.godtools.shared.tool.parser.expressions.grammar.generated.StateExpressionParser
import org.cru.godtools.shared.tool.parser.expressions.grammar.generated.StateExpressionParser.Tokens

internal class StateExpressionEvaluator(private val context: ExpressionContext) {
    val booleanExpr = object : StateExpressionBaseVisitor<Boolean>() {
        override fun visitParExpr(ctx: StateExpressionParser.ParExprContext) = ctx.expr!!.accept(this)
        override fun visitNotExpr(ctx: StateExpressionParser.NotExprContext) = !ctx.expr!!.accept(this)
        override fun visitOrExpr(ctx: StateExpressionParser.OrExprContext) =
            ctx.left!!.accept(this) || ctx.right!!.accept(this)

        override fun visitAndExpr(ctx: StateExpressionParser.AndExprContext) =
            ctx.left!!.accept(this) && ctx.right!!.accept(this)

        override fun visitBooleanAtom(ctx: StateExpressionParser.BooleanAtomContext) = when (ctx.atom!!.type) {
            Tokens.TRUE -> true
            Tokens.FALSE -> false
            else -> unexpectedToken(ctx.atom!!)
        }

        override fun visitEqExpr(ctx: StateExpressionParser.EqExprContext): Boolean {
            val varName = ctx.varName!!.text!!
            val value = ctx.value!!.text!!.run { substring(1, length - 1) }
            return when (ctx.op!!.type) {
                Tokens.EQ -> context.getVar(varName).contains(value)
                Tokens.NEQ -> !context.getVar(varName).contains(value)
                else -> unexpectedToken(ctx.op!!)
            }
        }

        override fun visitIntCmpExpr(ctx: StateExpressionParser.IntCmpExprContext): Boolean {
            val left = ctx.left!!.accept(intExpr)
            val right = ctx.right!!.accept(intExpr)
            return when (ctx.op!!.type) {
                Tokens.EQ -> left == right
                Tokens.NEQ -> left != right
                Tokens.GT -> left > right
                Tokens.GTE -> left >= right
                Tokens.LT -> left < right
                Tokens.LTE -> left <= right
                else -> unexpectedToken(ctx.op!!)
            }
        }

        override fun visitIsSetFunc(ctx: StateExpressionParser.IsSetFuncContext) =
            context.getVar(ctx.varName!!.text!!).isNotEmpty()

        override fun defaultResult() = false
    }

    val intExpr = object : StateExpressionBaseVisitor<Int>() {
        override fun visitIntAtom(ctx: StateExpressionParser.IntAtomContext) = ctx.value!!.text!!.toInt()

        override fun visitValuesFunc(ctx: StateExpressionParser.ValuesFuncContext) =
            context.getVar(ctx.varName!!.text!!).size

        override fun defaultResult() = 0
    }

    private fun unexpectedToken(token: Token): Nothing = error("Unexpected token: ${token.text}")
}
