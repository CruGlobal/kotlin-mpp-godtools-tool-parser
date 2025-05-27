@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import kotlinx.coroutines.CoroutineScope
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Clickable

internal fun Clickable.handleClickable(state: State, scope: CoroutineScope) {
    url?.let { state.triggerOpenUrlEvent(it) }
    state.triggerContentEvents(events)
}
