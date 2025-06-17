@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Clickable
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.Visibility

@Composable
internal fun Modifier.clickable(model: Clickable, state: State, scope: CoroutineScope): Modifier {
    val hasAnalyticsEvents = (model as? HasAnalyticsEvents)?.getAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED)
        ?.isNotEmpty() == true
    val isInvisible = when {
        model is Visibility -> remember(model, state) { model.isInvisibleFlow(state) }
            .collectAsState(model.isInvisible(state)).value
        else -> false
    }

    return when {
        (model.isClickable || hasAnalyticsEvents) && !isInvisible -> clickable { model.handleClickable(state, scope) }
        else -> this
    }
}

internal fun Clickable.handleClickable(state: State, scope: CoroutineScope) {
    url?.let { state.triggerOpenUrlEvent(it) }
    state.triggerContentEvents(events)
    if (this is HasAnalyticsEvents) triggerAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED, state, scope)
}
