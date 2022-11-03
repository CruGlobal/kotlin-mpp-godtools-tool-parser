package org.cru.godtools.shared.tool.parser.model

import io.github.aakira.napier.Napier
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.internal.DeprecationException
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.System.Companion.toAnalyticsSystems
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger.Companion.toTrigger
import org.cru.godtools.shared.tool.parser.util.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

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
private const val XML_SYSTEM_USER = "user"
private const val XML_TRIGGER = "trigger"
private const val XML_TRIGGER_CLICKED = "clicked"
private const val XML_TRIGGER_SELECTED = "selected"
private const val XML_TRIGGER_VISIBLE = "visible"
private const val XML_TRIGGER_HIDDEN = "hidden"
private const val XML_ATTRIBUTE = "attribute"
private const val XML_ATTRIBUTE_KEY = "key"
private const val XML_ATTRIBUTE_VALUE = "value"

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
            Napier.e(message, DeprecationException("XML Adobe Analytics Event $message"), TAG)
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    constructor(
        parent: Base = Manifest(),
        action: String? = null,
        trigger: Trigger = Trigger.DEFAULT,
        delay: Int = 0,
        systems: Set<System> = emptySet(),
        attributes: Map<String, String> = emptyMap()
    ) : super(parent) {
        this.action = action
        this.delay = delay
        this.systems = systems
        this.trigger = trigger
        this.attributes = attributes
    }

    fun isTriggerType(vararg types: Trigger) = types.contains(trigger)
    fun isForSystem(system: System) = systems.contains(system)

    enum class System {
        @Deprecated("Since 1/1/21, we no longer use Adobe analytics.")
        ADOBE,
        APPSFLYER, FACEBOOK, FIREBASE, SNOWPLOW, USER;

        internal companion object {
            fun String.toAnalyticsSystems() = REGEX_SEQUENCE_SEPARATOR.split(this).mapNotNullTo(mutableSetOf()) {
                when (it) {
                    XML_SYSTEM_ADOBE -> ADOBE
                    XML_SYSTEM_APPSFLYER -> APPSFLYER
                    XML_SYSTEM_FACEBOOK -> FACEBOOK
                    XML_SYSTEM_FIREBASE -> FIREBASE
                    XML_SYSTEM_SNOWPLOW -> SNOWPLOW
                    XML_SYSTEM_USER -> USER
                    else -> null
                }
            }
        }
    }

    enum class Trigger {
        @Deprecated(
            "Since 0.3.0 (9/3/21), use CLICKED instead",
            ReplaceWith("CLICKED", "org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger.CLICKED")
        )
        SELECTED,
        VISIBLE, HIDDEN, CLICKED, DEFAULT, UNKNOWN;

        internal companion object {
            fun String.toTrigger() = when (this) {
                XML_TRIGGER_CLICKED -> CLICKED
                XML_TRIGGER_SELECTED -> SELECTED
                XML_TRIGGER_VISIBLE -> VISIBLE
                XML_TRIGGER_HIDDEN -> HIDDEN
                else -> UNKNOWN
            }
        }
    }
}
