package org.cru.godtools.shared.tool.parser.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.renderer.state.State

@JsExport
@OptIn(ExperimentalJsExport::class)
class AnalyticsEvent {
    internal companion object {
        internal const val XML_EVENTS = "events"
    }

    private val _id: String?
    internal val id get() = _id ?: action
    val action: String
    val delay: Int
    val systems: Set<System>
    internal val trigger: Trigger
    internal val limit: Int?
    val attributes: Map<String, String>

    @JsName("createTestAnalyticsEvent")
    constructor(
        id: String? = null,
        action: String = "",
        trigger: Trigger = Trigger.DEFAULT,
        delay: Int = 0,
        systems: Set<System> = emptySet(),
        limit: Int? = null,
        attributes: Map<String, String> = emptyMap()
    ) : super() {
        _id = id
        this.action = action
        this.delay = delay
        this.systems = systems
        this.trigger = trigger
        this.limit = limit
        this.attributes = attributes
    }

    internal fun isTriggerType(vararg types: Trigger) = types.contains(trigger)
    fun isForSystem(system: System) = systems.contains(system)

    fun shouldTrigger(state: State) = limit == null || state.getTriggeredAnalyticsEventsCount(id) < limit
    fun recordTriggered(state: State) = state.recordTriggeredAnalyticsEvent(id)

    enum class System { FACEBOOK, FIREBASE, USER }
    enum class Trigger { VISIBLE, HIDDEN, CLICKED, DEFAULT, UNKNOWN }
}
