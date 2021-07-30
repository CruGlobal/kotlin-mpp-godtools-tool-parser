package org.cru.godtools.expressions.grammar

import org.cru.godtools.expressions.internal.grammar.ExpressionBaseVisitor
import org.cru.godtools.expressions.internal.grammar.ExpressionParser
import org.cru.godtools.tool.state.State

internal class ExpressionEvaluator(private val state: State) : ExpressionBaseVisitor<Boolean>() {
    override fun visitEqExpr(ctx: ExpressionParser.EqExprContext): Boolean {
        val varName = ctx.VAR()!!.text
        val value = ctx.STRING()!!.text.run { substring(1, length - 1) }
        return when(ctx.op!!.type) {
            ExpressionParser.Tokens.EQ.id -> state.getAll(varName).contains(value)
            ExpressionParser.Tokens.NEQ.id -> !state.getAll(varName).contains(value)
            else -> throw IllegalStateException()
        }
    }

    override fun visitBooleanAtom(ctx: ExpressionParser.BooleanAtomContext) = when(ctx.atom!!.type) {
        ExpressionParser.Tokens.TRUE.id -> true
        ExpressionParser.Tokens.FALSE.id -> false
        else -> throw IllegalStateException()
    }

    override fun visitNotExpr(ctx: ExpressionParser.NotExprContext) = !ctx.findExpr()!!.accept(this)

    override fun visitOrExpr(ctx: ExpressionParser.OrExprContext) = ctx.left!!.accept(this) || ctx.right!!.accept(this)
    override fun visitAndExpr(ctx: ExpressionParser.AndExprContext) =
        ctx.left!!.accept(this) && ctx.right!!.accept(this)
}
