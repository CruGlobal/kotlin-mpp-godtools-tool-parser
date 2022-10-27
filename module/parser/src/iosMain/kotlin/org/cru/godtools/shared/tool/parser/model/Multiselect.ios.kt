package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.util.FlowWatcher.Companion.watch
import org.cru.godtools.shared.tool.state.State

fun Multiselect.Option.watchIsSelected(state: State, block: (Boolean) -> Unit) = isSelectedFlow(state).watch(block)
