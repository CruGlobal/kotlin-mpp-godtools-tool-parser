package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_LABEL = "label"

class Tab internal constructor(parent: Tabs, val position: Int, parser: XmlPullParser) : BaseModel(parent), Parent {
    companion object {
        internal const val XML_TAB = "tab"
    }

    val analyticsEvents: Collection<AnalyticsEvent>
    val listeners: Set<EventId>
    val label: Text?

    override val content: List<Content>
    val tips get() = contentTips

    init {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TAB)
        listeners = parser.getAttributeValue(XML_LISTENERS)?.toEventIds()?.toSet().orEmpty()

        // process any child elements
        var analyticsEvents: Collection<AnalyticsEvent> = emptyList()
        var label: Text? = null
        content = parseContent(parser) {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }
                XMLNS_CONTENT -> when (parser.name) {
                    XML_LABEL -> label = parser.parseTextChild(this, XMLNS_CONTENT, XML_LABEL)
                }
            }
        }
        this.analyticsEvents = analyticsEvents
        this.label = label
    }
}
