package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.Base
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.TEXT_SIZE_BASE
import org.cru.godtools.tool.model.TEXT_SIZE_HERO_HEADING
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.XMLNS_ANALYTICS
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.model.primaryColor
import org.cru.godtools.tool.model.stylesOverride
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_HEADING = "heading"

class Hero : BaseModel, Parent {
    internal companion object {
        internal const val XML_HERO = "hero"
    }

    val analyticsEvents: List<AnalyticsEvent>
    private val headingParent by lazy {
        stylesOverride(
            textColor = { stylesParent.primaryColor },
            textScale = TEXT_SIZE_HERO_HEADING.toDouble() / TEXT_SIZE_BASE
        )
    }
    val heading: Text?
    override val content: List<Content>

    internal constructor(page: TractPage, parser: XmlPullParser) : super(page) {
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
                    XML_HEADING -> heading = parser.parseTextChild(headingParent, XMLNS_TRACT, XML_HEADING)
                }
            }
        }
        this.heading = heading
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
        page: TractPage = TractPage(),
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        heading: (Base) -> Text?
    ) : super(page) {
        this.analyticsEvents = analyticsEvents
        this.heading = heading(headingParent)
        content = emptyList()
    }
}
