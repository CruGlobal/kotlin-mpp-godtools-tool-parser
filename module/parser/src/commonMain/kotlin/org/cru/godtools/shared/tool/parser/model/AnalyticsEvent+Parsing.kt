@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.util.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_EVENT = "event"
private const val XML_ID = "id"
private const val XML_ACTION = "action"
private const val XML_DELAY = "delay"
private const val XML_SYSTEM = "system"
private const val XML_SYSTEM_FACEBOOK = "facebook"
private const val XML_SYSTEM_FIREBASE = "firebase"
private const val XML_SYSTEM_USER = "user"
private const val XML_TRIGGER = "trigger"
private const val XML_TRIGGER_CLICKED = "clicked"
private const val XML_TRIGGER_VISIBLE = "visible"
private const val XML_TRIGGER_HIDDEN = "hidden"
private const val XML_LIMIT = "limit"
private const val XML_ATTRIBUTE = "attribute"
private const val XML_ATTRIBUTE_KEY = "key"
private const val XML_ATTRIBUTE_VALUE = "value"

internal val AnalyticsEvent.Companion.XML_EVENTS get() = "events"

internal fun XmlPullParser.parseAnalyticsEvents() = buildList {
    require(XmlPullParser.START_TAG, XMLNS_ANALYTICS, AnalyticsEvent.XML_EVENTS)
    parseChildren {
        when (namespace) {
            XMLNS_ANALYTICS -> when (name) {
                XML_EVENT -> add(parseAnalyticsEvent())
            }
        }
    }
}

internal fun XmlPullParser.parseAnalyticsEvent(): AnalyticsEvent {
    require(XmlPullParser.START_TAG, XMLNS_ANALYTICS, XML_EVENT)

    val action = getAttributeValue(XML_ACTION).orEmpty()
    return AnalyticsEvent(
        action = action,
        id = getAttributeValue(XML_ID) ?: action,
        delay = getAttributeValue(XML_DELAY)?.toIntOrNull() ?: 0,
        systems = getAttributeValue(XML_SYSTEM)?.toAnalyticsEventSystems().orEmpty(),
        trigger = getAttributeValue(XML_TRIGGER)?.toAnalyticsEventTrigger() ?: AnalyticsEvent.Trigger.DEFAULT,
        limit = getAttributeValue(XML_LIMIT)?.toIntOrNull(),
        attributes = buildMap {
            parseChildren {
                when (namespace) {
                    XMLNS_ANALYTICS -> when (name) {
                        XML_ATTRIBUTE -> put(
                            getAttributeValue(XML_ATTRIBUTE_KEY).orEmpty(),
                            getAttributeValue(XML_ATTRIBUTE_VALUE).orEmpty()
                        )
                    }
                }
            }
        }
    )
}

internal fun String.toAnalyticsEventSystems() = REGEX_SEQUENCE_SEPARATOR.split(this)
    .mapNotNullTo(mutableSetOf()) {
        when (it) {
            XML_SYSTEM_FACEBOOK -> AnalyticsEvent.System.FACEBOOK
            XML_SYSTEM_FIREBASE -> AnalyticsEvent.System.FIREBASE
            XML_SYSTEM_USER -> AnalyticsEvent.System.USER
            else -> null
        }
    }

internal fun String.toAnalyticsEventTrigger() = when (this) {
    XML_TRIGGER_CLICKED -> AnalyticsEvent.Trigger.CLICKED
    XML_TRIGGER_VISIBLE -> AnalyticsEvent.Trigger.VISIBLE
    XML_TRIGGER_HIDDEN -> AnalyticsEvent.Trigger.HIDDEN
    else -> AnalyticsEvent.Trigger.UNKNOWN
}
