package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.AnalyticsEvent.System.Companion.toAnalyticsSystems
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger.Companion.toTrigger
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val TAG = "XmlAnalyticsEvent"

private const val XML_EVENT = "event"
private const val XML_ACTION = "action"
private const val XML_DELAY = "delay"
private const val XML_SYSTEM = "system"
private const val XML_SYSTEM_ADOBE = "adobe"
private const val XML_SYSTEM_APPSFLYER = "appsflyer"
private const val XML_SYSTEM_FACEBOOK = "facebook"
private const val XML_SYSTEM_FIREBASE = "firebase"
private const val XML_SYSTEM_SNOWPLOW = "snowplow"
private const val XML_TRIGGER = "trigger"
private const val XML_TRIGGER_SELECTED = "selected"
private const val XML_TRIGGER_VISIBLE = "visible"
private const val XML_TRIGGER_HIDDEN = "hidden"
private const val XML_ATTRIBUTE = "attribute"
private const val XML_ATTRIBUTE_KEY = "key"
private const val XML_ATTRIBUTE_VALUE = "value"

@OptIn(ExperimentalStdlibApi::class)
class AnalyticsEvent : BaseModel {
    internal companion object {
        internal const val XML_EVENTS = "events"

        fun XmlPullParser.parseAnalyticsEvents(parent: Base) = buildList {
            require(XmlPullParser.START_TAG, XMLNS_ANALYTICS, XML_EVENTS)
            parseChildren {
                when (namespace) {
                    XMLNS_ANALYTICS -> when (name) {
                        XML_EVENT -> add(AnalyticsEvent(parent, this@parseAnalyticsEvents))
                    }
                }
            }
        }
    }

    val action: String?
    val delay: Int
    val systems: Set<System>
    val trigger: Trigger
    val attributes: Map<String, String>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        parser.require(XmlPullParser.START_TAG, XMLNS_ANALYTICS, XML_EVENT)

        action = parser.getAttributeValue(XML_ACTION)
        delay = parser.getAttributeValue(XML_DELAY)?.toIntOrNull() ?: 0
        systems = parser.getAttributeValue(XML_SYSTEM)?.toAnalyticsSystems().orEmpty()
        trigger = parser.getAttributeValue(XML_TRIGGER)?.toTrigger() ?: Trigger.DEFAULT
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

        // Log a non-fatal warning if this is an adobe analytics event
        if (systems.contains(System.ADOBE)) {
            val message = "tool: ${manifest.code} locale: ${manifest.locale} action: $action"
            Napier.e(message, UnsupportedOperationException("XML Adobe Analytics Event $message"), TAG)
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
        action: String? = null,
        delay: Int = 0,
        systems: Set<System> = emptySet(),
        attributes: Map<String, String> = emptyMap()
    ) {
        this.action = action
        this.delay = delay
        this.systems = systems
        trigger = Trigger.DEFAULT
        this.attributes = attributes
    }

    fun isTriggerType(vararg types: Trigger) = types.contains(trigger)
    fun isForSystem(system: System) = systems.contains(system)

    enum class System {
        ADOBE, APPSFLYER, FACEBOOK, FIREBASE, SNOWPLOW;

        internal companion object {
            fun String.toAnalyticsSystems() = REGEX_SEQUENCE_SEPARATOR.split(this).mapNotNullTo(mutableSetOf()) {
                when (it) {
                    XML_SYSTEM_ADOBE -> ADOBE
                    XML_SYSTEM_APPSFLYER -> APPSFLYER
                    XML_SYSTEM_FACEBOOK -> FACEBOOK
                    XML_SYSTEM_FIREBASE -> FIREBASE
                    XML_SYSTEM_SNOWPLOW -> SNOWPLOW
                    else -> null
                }
            }
        }
    }

    enum class Trigger {
        SELECTED, VISIBLE, HIDDEN, DEFAULT, UNKNOWN;

        internal companion object {
            fun String.toTrigger() = when (this) {
                XML_TRIGGER_SELECTED -> SELECTED
                XML_TRIGGER_VISIBLE -> VISIBLE
                XML_TRIGGER_HIDDEN -> HIDDEN
                else -> UNKNOWN
            }
        }
    }
}
