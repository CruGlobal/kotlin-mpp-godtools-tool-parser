@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents

internal fun HasAnalyticsEvents.triggerAnalyticsEvents(
    trigger: AnalyticsEvent.Trigger,
    state: State,
    delayScope: CoroutineScope,
) = getAnalyticsEvents(trigger)
    .filter { state.shouldTriggerAnalyticsEvent(it) }
    .map {
        delayScope.launch(start = CoroutineStart.UNDISPATCHED) {
            delay(it.delay.seconds)
            state.triggerAnalyticsEvent(it)
        }
    }
    .filterNot { it.isCompleted }
