package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

class Link : Content, HasAnalyticsEvents, Clickable {
    internal companion object {
        internal const val XML_LINK = "link"
    }

    val analyticsEvents: List<AnalyticsEvent>
    override val events: List<EventId>
    override val url: Uri?

    private val defaultTextStyles by lazy { stylesOverride(textColor = { stylesParent.primaryColor }) }
    val text: Text

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_LINK)
        parseClickableAttrs(parser) { events, url ->
            this.events = events
            this.url = url
        }
        analyticsEvents = mutableListOf()
        text = parser.parseTextChild(defaultTextStyles, XMLNS_CONTENT, XML_LINK) {
            when (parser.namespace) {
                XMLNS_ANALYTICS ->
                    when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                    }
            }
        } ?: Text(defaultTextStyles)
    }

    override val isIgnored get() = super.isIgnored || !isClickable

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        events: List<EventId> = emptyList(),
        url: Uri? = null,
        text: ((Base) -> Text?)? = null
    ) : super(parent) {
        this.analyticsEvents = analyticsEvents
        this.events = events
        this.url = url
        this.text = text?.invoke(defaultTextStyles) ?: Text(defaultTextStyles)
    }

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.CLICKED -> analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.DEFAULT) }
        else -> error("The $type trigger type is currently unsupported on Links")
    }
}
