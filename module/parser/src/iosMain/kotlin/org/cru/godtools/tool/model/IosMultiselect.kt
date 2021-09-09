package org.cru.godtools.tool.model

import org.cru.godtools.tool.state.State
import org.cru.godtools.tool.util.watch

fun Multiselect.Option.watchIsSelected(state: State, block: (Boolean) -> Unit) = isSelectedFlow(state).watch(block)
