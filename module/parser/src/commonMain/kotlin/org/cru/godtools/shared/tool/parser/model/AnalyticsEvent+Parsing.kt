@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.util.REGEX_SEQUENCE_SEPARATOR

private const val XML_SYSTEM_FACEBOOK = "facebook"
private const val XML_SYSTEM_FIREBASE = "firebase"
private const val XML_SYSTEM_USER = "user"
private const val XML_TRIGGER_CLICKED = "clicked"
private const val XML_TRIGGER_VISIBLE = "visible"
private const val XML_TRIGGER_HIDDEN = "hidden"

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
