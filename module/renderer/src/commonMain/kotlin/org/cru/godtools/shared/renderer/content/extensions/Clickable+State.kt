@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Clickable
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.Visibility
import org.cru.godtools.shared.tool.parser.model.formParent

@Composable
internal fun Modifier.clickable(
    model: Clickable,
    state: State,
    focusManager: FocusManager = LocalFocusManager.current,
    scope: CoroutineScope = rememberCoroutineScope(),
) = when {
    model.isClickableModifierNeeded(state) -> clickable { model.handleClickable(state, focusManager, scope) }
    else -> this
}

@Composable
internal fun Modifier.clickable(
    model: Clickable,
    state: State,
    indication: Indication?,
    interactionSource: MutableInteractionSource? = null,
    focusManager: FocusManager = LocalFocusManager.current,
    scope: CoroutineScope = rememberCoroutineScope(),
) = when {
    model.isClickableModifierNeeded(state) ->
        clickable(interactionSource, indication) { model.handleClickable(state, focusManager, scope) }

    else -> this
}

@Composable
private fun Clickable.isClickableModifierNeeded(state: State): Boolean {
    val hasAnalyticsEvents = (this as? HasAnalyticsEvents)?.getAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED)
        ?.isNotEmpty() == true
    val isInvisible = when {
        this is Visibility -> produceIsInvisible(state).value
        else -> false
    }

    return (isClickable || hasAnalyticsEvents) && !isInvisible
}

internal fun Clickable.handleClickable(state: State, focusManager: FocusManager, scope: CoroutineScope) {
    // short-circuit the click if we are submitting a form, but form validation fails
    if (EventId.FOLLOWUP in events && formParent?.submitForm(state) == false) return

    focusManager.clearFocus()
    url?.let { state.triggerEvent(State.Event.OpenUrl(it)) }
    state.triggerContentEvents(events.filterNot { it == EventId.FOLLOWUP })
    if (this is HasAnalyticsEvents) triggerAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED, state, scope)
}
