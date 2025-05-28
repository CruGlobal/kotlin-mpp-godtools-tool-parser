@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.renderer.state.State

@Suppress("NullableBooleanElvis")
fun AnalyticsEvent.shouldTrigger(state: State) = limit?.let { state.getTriggeredAnalyticsEventsCount(id) < it } ?: true
fun AnalyticsEvent.recordTriggered(state: State) = state.recordTriggeredAnalyticsEvent(id)
