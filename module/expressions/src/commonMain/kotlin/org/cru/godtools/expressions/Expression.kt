package org.cru.godtools.expressions

import org.antlr.v4.kotlinruntime.BailErrorStrategy
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.misc.ParseCancellationException
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.cru.godtools.expressions.grammar.StateExpressionEvaluator
import org.cru.godtools.expressions.grammar.generated.StateExpressionLexer
import org.cru.godtools.expressions.grammar.generated.StateExpressionParser
import org.cru.godtools.tool.state.State

class Expression internal constructor(private val expr: StateExpressionParser.BooleanExprContext?) {
    fun isValid() = expr != null
    fun evaluate(state: State) = checkNotNull(expr).accept(StateExpressionEvaluator(state).booleanExpr)
    fun vars() = expr?.vars()?.toSet().orEmpty()
}

fun String.toExpressionOrNull() = when {
    isBlank() -> null
    else -> Expression(
        try {
            val tokens = CommonTokenStream(StateExpressionLexer(CharStreams.fromString(this)))
            val parser = StateExpressionParser(tokens)
            parser.errorHandler = BailErrorStrategy()
            val expr = parser.booleanExpr()
            if (tokens[tokens.index()].type == Token.EOF) expr else null
        } catch (e: ParseCancellationException) {
            // TODO: log the exception to Napier, useful to identify invalid/unsupported expressions
            null
        }
    )
}

private fun ParserRuleContext.vars(): Collection<String>? = children?.flatMap {
    when (it) {
        is TerminalNode -> listOfNotNull(it.symbol?.takeIf { it.type == StateExpressionParser.Tokens.VAR.id }?.text)
        is ParserRuleContext -> it.vars()
        else -> null
    }
}
