package org.cru.godtools.expressions.grammar

import org.cru.godtools.expressions.internal.grammar.StateExpressionBaseVisitor
import org.cru.godtools.expressions.internal.grammar.StateExpressionParser
import org.cru.godtools.tool.state.State

internal class StateExpressionEvaluator(private val state: State) : StateExpressionBaseVisitor<Boolean>() {
    override fun visitParExpr(ctx: StateExpressionParser.ParExprContext) = ctx.expr!!.accept(this)
    override fun visitNotExpr(ctx: StateExpressionParser.NotExprContext) = !ctx.expr!!.accept(this)
    override fun visitOrExpr(ctx: StateExpressionParser.OrExprContext) =
        ctx.left!!.accept(this) || ctx.right!!.accept(this)
    override fun visitAndExpr(ctx: StateExpressionParser.AndExprContext) =
        ctx.left!!.accept(this) && ctx.right!!.accept(this)

    override fun visitBooleanAtom(ctx: StateExpressionParser.BooleanAtomContext) = when (ctx.atom!!.type) {
        StateExpressionParser.Tokens.TRUE.id -> true
        StateExpressionParser.Tokens.FALSE.id -> false
        else -> throw IllegalStateException()
    }

    override fun visitEqExpr(ctx: StateExpressionParser.EqExprContext): Boolean {
        val varName = ctx.varName!!.text!!
        val value = ctx.value!!.text!!.run { substring(1, length - 1) }
        return when (ctx.op!!.type) {
            StateExpressionParser.Tokens.EQ.id -> state.getAll(varName).contains(value)
            StateExpressionParser.Tokens.NEQ.id -> !state.getAll(varName).contains(value)
            else -> throw IllegalStateException()
        }
    }

    override fun visitIsSetFunc(ctx: StateExpressionParser.IsSetFuncContext) =
        !state.getAll(ctx.varName!!.text!!).isEmpty()
}