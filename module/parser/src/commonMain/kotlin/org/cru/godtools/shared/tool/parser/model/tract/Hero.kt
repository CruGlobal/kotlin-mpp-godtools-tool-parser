package org.cru.godtools.shared.tool.parser.model.tract

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Base
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.XML_EVENTS
import org.cru.godtools.shared.tool.parser.model.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.model.parseTextChild
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.stylesOverride
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

private const val XML_HEADING = "heading"

@JsExport
@OptIn(ExperimentalJsExport::class)
class Hero : BaseModel, Parent, HasAnalyticsEvents {
    internal companion object {
        internal const val XML_HERO = "hero"
    }

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
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                }

                XMLNS_TRACT -> when (parser.name) {
                    XML_HEADING -> heading = parser.parseTextChild(headingParent, XMLNS_TRACT, XML_HEADING)
                }
            }
        }
        this.heading = heading
    }

    @JsName("createTestHero")
    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
        page: TractPage = TractPage(),
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        heading: ((Base) -> Text?)? = null
    ) : super(page) {
        this.analyticsEvents = analyticsEvents
        this.heading = heading?.invoke(headingParent)
        content = emptyList()
    }

    // region HasAnalyticsEvents
    @VisibleForTesting
    internal val analyticsEvents: List<AnalyticsEvent>

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
        Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
        else -> error("Analytics trigger type $type is not currently supported on Heroes")
    }
    // endregion HasAnalyticsEvents
}
