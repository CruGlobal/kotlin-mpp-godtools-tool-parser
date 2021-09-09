package org.cru.godtools.tool.model

import org.cru.godtools.tool.state.State
import org.cru.godtools.tool.util.watch

fun Content.watchIsGone(state: State, block: (Boolean) -> Unit) = isGoneFlow(state).watch(block)
fun Content.watchIsInvisible(state: State, block: (Boolean) -> Unit) = isInvisibleFlow(state).watch(block)
