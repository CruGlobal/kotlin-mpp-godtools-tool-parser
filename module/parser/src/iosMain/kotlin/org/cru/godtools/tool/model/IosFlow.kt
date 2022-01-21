package org.cru.godtools.tool.model

import kotlinx.coroutines.flow.combine
import org.cru.godtools.tool.state.State
import org.cru.godtools.tool.util.watch

fun Flow.Item.watchIsGone(state: State, block: (Boolean) -> Unit) = isGoneFlow(state).watch(block)
fun Flow.Item.watchIsInvisible(state: State, block: (Boolean) -> Unit) = isInvisibleFlow(state).watch(block)

fun Flow.Item.watchVisibility(state: State, block: (Boolean, Boolean) -> Unit) =
    isInvisibleFlow(state).combine(isGoneFlow(state)) { invisible, gone -> Pair(invisible, gone) }
        .watch { block(it.first, it.second) }
