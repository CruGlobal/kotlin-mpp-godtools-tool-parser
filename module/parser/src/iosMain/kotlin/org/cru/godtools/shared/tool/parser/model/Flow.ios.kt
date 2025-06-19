package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.expressions.ExpressionContext
import org.cru.godtools.shared.tool.parser.util.FlowWatcher
import org.cru.godtools.shared.tool.parser.util.FlowWatcher.Companion.watch

fun Flow.watchItems(ctx: ExpressionContext, block: (List<Flow.Item>) -> Unit): FlowWatcher {
    val vars = items.flatMapTo(mutableSetOf()) { it.invisibleIf?.vars().orEmpty() + it.goneIf?.vars().orEmpty() }
    return ctx.varsChangeFlow(vars) { items.filter { !it.isGone(ctx) } }.watch(block)
}
