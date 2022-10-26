package org.cru.godtools.tool.model

import kotlinx.coroutines.flow.combine
import org.cru.godtools.shared.tool.state.State
import org.cru.godtools.tool.util.FlowWatcher.Companion.watch

fun Content.watchIsGone(state: State, block: (Boolean) -> Unit) = isGoneFlow(state).watch(block)
fun Content.watchIsInvisible(state: State, block: (Boolean) -> Unit) = isInvisibleFlow(state).watch(block)

fun Content.watchVisibility(state: State, block: (Boolean, Boolean) -> Unit) =
    isInvisibleFlow(state).combine(isGoneFlow(state)) { invisible, gone -> Pair(invisible, gone) }
        .watch { block(it.first, it.second) }
