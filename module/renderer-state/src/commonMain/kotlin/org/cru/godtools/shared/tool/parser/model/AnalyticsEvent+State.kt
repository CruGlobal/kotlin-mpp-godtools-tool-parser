@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.renderer.state.State

fun AnalyticsEvent.shouldTrigger(state: State) = state.shouldTriggerAnalyticsEvent(this)
fun AnalyticsEvent.recordTriggered(state: State) = state.recordTriggeredAnalyticsEvent(this)
