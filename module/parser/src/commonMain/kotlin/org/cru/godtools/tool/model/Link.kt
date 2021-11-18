package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.xml.XmlPullParser

class Link : Content, Styles, HasAnalyticsEvents, Clickable {
    internal companion object {
        internal const val XML_LINK = "link"
    }

    override val textColor get() = primaryColor

    val analyticsEvents: List<AnalyticsEvent>
    override val events: List<EventId>
    override val url: Uri?
    val text: Text?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_LINK)
        parser.parseClickableAttrs { events, url ->
            this.events = events
            this.url = url
        }
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
        this.text = text?.invoke(this)
    }

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.CLICKED -> analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.DEFAULT) }
        else -> error("The $type trigger type is currently unsupported on Links")
    }
}
