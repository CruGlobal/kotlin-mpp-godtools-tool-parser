package org.cru.godtools.expressions.grammar

import org.cru.godtools.expressions.internal.grammar.ExpressionBaseVisitor
import org.cru.godtools.expressions.internal.grammar.ExpressionParser
import org.cru.godtools.tool.state.State

internal class ExpressionEvaluator(private val state: State) : ExpressionBaseVisitor<Boolean>() {
    override fun visitEqExpr(ctx: ExpressionParser.EqExprContext): Boolean {
        val varName = ctx.VAR()!!.text
        val value = ctx.STRING()!!.text.run { substring(1, length - 1) }
        return when {
            ctx.EQ() != null -> state.getAll(varName).contains(value)
            ctx.NEQ() != null -> !state.getAll(varName).contains(value)
            else -> throw IllegalStateException()
        }
    }

    override fun visitBooleanAtom(ctx: ExpressionParser.BooleanAtomContext) = when {
        ctx.TRUE() != null -> true
        ctx.FALSE() != null -> false
        else -> throw IllegalStateException()
    }

    override fun visitNotExpr(ctx: ExpressionParser.NotExprContext) = !ctx.findExpr()!!.accept(this)
}
