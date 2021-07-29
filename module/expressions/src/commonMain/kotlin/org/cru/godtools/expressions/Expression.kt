package org.cru.godtools.expressions

import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.cru.godtools.expressions.grammar.ExpressionEvaluator
import org.cru.godtools.expressions.internal.grammar.ExpressionLexer
import org.cru.godtools.expressions.internal.grammar.ExpressionParser
import org.cru.godtools.tool.state.State

class Expression internal constructor(private val expr: ExpressionParser.ExprContext) {
    fun isValid() = expr.exception == null
    fun evaluate(state: State) = expr.accept(ExpressionEvaluator(state))
}

fun String.toExpressionOrNull() = when {
    isNullOrBlank() -> null
    else -> Expression(ExpressionParser(CommonTokenStream(ExpressionLexer(CharStreams.fromString(this)))).expr())
}
