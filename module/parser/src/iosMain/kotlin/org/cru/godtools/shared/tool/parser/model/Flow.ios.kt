package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.util.FlowWatcher
import org.cru.godtools.shared.tool.parser.util.FlowWatcher.Companion.watch

fun Flow.watchItems(state: State, block: (List<Flow.Item>) -> Unit): FlowWatcher {
    val vars = items.flatMapTo(mutableSetOf()) { it.invisibleIf?.vars().orEmpty() + it.goneIf?.vars().orEmpty() }
    return state.varsChangeFlow(vars) { items.filter { !it.isGone(state) } }.watch(block)
}
