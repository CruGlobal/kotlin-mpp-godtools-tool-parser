package org.cru.godtools.expressions

import org.antlr.v4.kotlinruntime.BailErrorStrategy
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.misc.ParseCancellationException
import org.cru.godtools.expressions.grammar.ExpressionEvaluator
import org.cru.godtools.expressions.internal.grammar.ExpressionLexer
import org.cru.godtools.expressions.internal.grammar.ExpressionParser
import org.cru.godtools.tool.state.State

class Expression internal constructor(private val expr: ExpressionParser.BooleanExprContext?) {
    fun isValid() = expr != null
    fun evaluate(state: State) = checkNotNull(expr).accept(ExpressionEvaluator(state))
}

fun String.toExpressionOrNull() = when {
    isBlank() -> null
    else -> Expression(
        try {
            val tokens = CommonTokenStream(ExpressionLexer(CharStreams.fromString(this)))
            val parser = ExpressionParser(tokens)
            parser.errorHandler = BailErrorStrategy()
            val expr = parser.booleanExpr()
            if (tokens[tokens.index()].type == Token.EOF) expr else null
        } catch (e: ParseCancellationException) {
            // TODO: log the exception to Napier, useful to identify invalid/unsupported expressions
            null
        }
    )
}
