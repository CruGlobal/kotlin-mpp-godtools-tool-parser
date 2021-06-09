package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.xml.XmlPullParser

class Link internal constructor(parent: Base, parser: XmlPullParser) : Content(parent, parser) {
    companion object {
        internal const val XML_LINK = "link"
    }

    val analyticsEvents: Collection<AnalyticsEvent>
    val events: List<EventId>
    val text: Text?

    init {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_LINK)
        events = parser.getAttributeValue(XML_EVENTS)?.toEventIds().orEmpty()

        // process any child elements
        val analyticsEvents = mutableListOf<AnalyticsEvent>()
        text = parser.parseTextChild(this, XMLNS_CONTENT, XML_LINK) {
            when (parser.namespace) {
                XMLNS_ANALYTICS ->
                    when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                    }
            }
        }
        this.analyticsEvents = analyticsEvents
    }
}
