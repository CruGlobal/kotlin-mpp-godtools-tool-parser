package org.cru.godtools.shared.tool.parser.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.renderer.state.State

@JsExport
@OptIn(ExperimentalJsExport::class)
data class AnalyticsEvent(
    val action: String = "",
    val attributes: Map<String, String> = emptyMap(),
    private val trigger: Trigger = Trigger.DEFAULT,
    val delay: Int = 0,
    private val systems: Set<System> = emptySet(),
    internal val id: String = action,
    internal val limit: Int? = null,
) {
    internal companion object {
        internal const val XML_EVENTS = "events"
    }

    internal fun isTriggerType(vararg types: Trigger) = types.contains(trigger)
    fun isForSystem(system: System) = systems.contains(system)

    fun shouldTrigger(state: State) = limit == null || state.getTriggeredAnalyticsEventsCount(id) < limit
    fun recordTriggered(state: State) = state.recordTriggeredAnalyticsEvent(id)

    enum class System { FACEBOOK, FIREBASE, USER }
    enum class Trigger { VISIBLE, HIDDEN, CLICKED, DEFAULT, UNKNOWN }
}
