package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.xml.XmlPullParser

class Link : Content {
    internal companion object {
        internal const val XML_LINK = "link"
    }

    val analyticsEvents: List<AnalyticsEvent>
    val events: List<EventId>
    val text: Text?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_LINK)
        events = parser.getAttributeValue(XML_EVENTS)?.toEventIds().orEmpty()
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
}
