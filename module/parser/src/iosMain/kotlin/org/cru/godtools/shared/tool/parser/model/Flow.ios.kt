package org.cru.godtools.shared.tool.parser.model

import kotlinx.coroutines.flow.combine
import org.cru.godtools.shared.tool.parser.util.FlowWatcher
import org.cru.godtools.shared.tool.parser.util.FlowWatcher.Companion.watch
import org.cru.godtools.shared.tool.state.State
import org.cru.godtools.shared.tool.state.varsChangeFlow

fun Flow.watchItems(state: State, block: (List<Flow.Item>) -> Unit): FlowWatcher {
    val vars = items.flatMapTo(mutableSetOf()) { it.invisibleIf?.vars().orEmpty() + it.goneIf?.vars().orEmpty() }
    return state.varsChangeFlow(vars) { items.filter { !it.isGone(state) } }.watch(block)
}

fun Flow.Item.watchIsGone(state: State, block: (Boolean) -> Unit) = isGoneFlow(state).watch(block)
fun Flow.Item.watchIsInvisible(state: State, block: (Boolean) -> Unit) = isInvisibleFlow(state).watch(block)

fun Flow.Item.watchVisibility(state: State, block: (Boolean, Boolean) -> Unit) =
    isInvisibleFlow(state).combine(isGoneFlow(state)) { invisible, gone -> Pair(invisible, gone) }
        .watch { block(it.first, it.second) }
