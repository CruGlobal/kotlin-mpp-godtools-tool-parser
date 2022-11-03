package org.cru.godtools.shared.tool.parser.model.tract

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Base
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.model.parseTextChild
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.stylesOverride
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

private const val XML_HEADING = "heading"

class Hero : BaseModel, Parent, HasAnalyticsEvents {
    internal companion object {
        internal const val XML_HERO = "hero"
    }

    val analyticsEvents: List<AnalyticsEvent>
    private val headingParent by lazy { stylesOverride(textColor = { stylesParent.primaryColor }) }
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

    @RestrictTo(RestrictToScope.TESTS)
    constructor(
        page: TractPage = TractPage(),
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        heading: ((Base) -> Text?)? = null
    ) : super(page) {
        this.analyticsEvents = analyticsEvents
        this.heading = heading?.invoke(headingParent)
        content = emptyList()
    }

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
        Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
        else -> error("Analytics trigger type $type is not currently supported on Heroes")
    }
}
