package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.Base
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.XMLNS_ANALYTICS
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.xml.XmlPullParser

class Hero : BaseModel, Parent, Styles {
    internal companion object {
        internal const val XML_HERO = "hero"

        private const val XML_HEADING = "heading"
    }

    val analyticsEvents: Collection<AnalyticsEvent>
    val heading: Text?
    override val content: List<Content>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_HERO)

        // process any child elements
        analyticsEvents = mutableListOf()
        var heading: Text? = null
        content = parseContent(parser) {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }
                XMLNS_TRACT -> when (parser.name) {
                    XML_HEADING -> heading = parser.parseTextChild(this, XMLNS_TRACT, XML_HEADING)
                }
            }
        }
        this.heading = heading
    }
}
