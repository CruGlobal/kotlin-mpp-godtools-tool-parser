package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.xml.XmlPullParser

class Link : Content, Styles, HasAnalyticsEvents {
    internal companion object {
        internal const val XML_LINK = "link"
    }

    override val textColor get() = primaryColor

    val analyticsEvents: List<AnalyticsEvent>
    val events: List<EventId>
    val text: Text?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_LINK)
        events = parser.getAttributeValue(XML_EVENTS).toEventIds()
        analyticsEvents = mutableListOf()
        text = parser.parseTextChild(this, XMLNS_CONTENT, XML_LINK) {
            when (parser.namespace) {
                XMLNS_ANALYTICS ->
                    when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                    }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        text: ((Base) -> Text?)? = null
    ) : super(parent) {
        this.analyticsEvents = analyticsEvents
        events = emptyList()
        this.text = text?.invoke(this)
    }

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.SELECTED -> analyticsEvents.filter { it.isTriggerType(Trigger.SELECTED, Trigger.DEFAULT) }
        else -> error("The $type trigger type is currently unsupported on Links")
    }
}
