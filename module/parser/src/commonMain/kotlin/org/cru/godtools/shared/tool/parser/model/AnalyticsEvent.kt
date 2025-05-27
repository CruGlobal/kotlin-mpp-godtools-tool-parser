package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_EVENT = "event"
private const val XML_ID = "id"
private const val XML_ACTION = "action"
private const val XML_DELAY = "delay"
private const val XML_SYSTEM = "system"
private const val XML_TRIGGER = "trigger"
private const val XML_LIMIT = "limit"
private const val XML_ATTRIBUTE = "attribute"
private const val XML_ATTRIBUTE_KEY = "key"
private const val XML_ATTRIBUTE_VALUE = "value"

@JsExport
@OptIn(ExperimentalJsExport::class)
class AnalyticsEvent {
    internal companion object {
        internal const val XML_EVENTS = "events"

        fun XmlPullParser.parseAnalyticsEvents() = buildList {
            require(XmlPullParser.START_TAG, XMLNS_ANALYTICS, XML_EVENTS)
            parseChildren {
                when (namespace) {
                    XMLNS_ANALYTICS -> when (name) {
                        XML_EVENT -> add(AnalyticsEvent(this@parseAnalyticsEvents))
                    }
                }
            }
        }
    }

    private val _id: String?
    internal val id get() = _id ?: action
    val action: String
    val delay: Int
    val systems: Set<System>
    internal val trigger: Trigger
    internal val limit: Int?
    val attributes: Map<String, String>

    internal constructor(parser: XmlPullParser) : super() {
        parser.require(XmlPullParser.START_TAG, XMLNS_ANALYTICS, XML_EVENT)

        _id = parser.getAttributeValue(XML_ID)
        action = parser.getAttributeValue(XML_ACTION).orEmpty()
        delay = parser.getAttributeValue(XML_DELAY)?.toIntOrNull() ?: 0
        systems = parser.getAttributeValue(XML_SYSTEM)?.toAnalyticsEventSystems().orEmpty()
        trigger = parser.getAttributeValue(XML_TRIGGER)?.toAnalyticsEventTrigger() ?: Trigger.DEFAULT
        limit = parser.getAttributeValue(XML_LIMIT)?.toIntOrNull()
        attributes = buildMap {
            parser.parseChildren {
                when (parser.namespace) {
                    XMLNS_ANALYTICS -> when (parser.name) {
                        XML_ATTRIBUTE -> put(
                            parser.getAttributeValue(XML_ATTRIBUTE_KEY).orEmpty(),
                            parser.getAttributeValue(XML_ATTRIBUTE_VALUE).orEmpty()
                        )
                    }
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
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
