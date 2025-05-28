@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import kotlinx.coroutines.CoroutineScope
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Clickable
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents

internal fun Clickable.handleClickable(state: State, scope: CoroutineScope) {
    url?.let { state.triggerOpenUrlEvent(it) }
    state.triggerContentEvents(events)
    if (this is HasAnalyticsEvents) triggerAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED, state, scope)
}
