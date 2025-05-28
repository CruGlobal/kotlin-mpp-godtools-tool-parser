package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

@JsExport
@OptIn(ExperimentalJsExport::class)
class Link : Content, HasAnalyticsEvents, Clickable {
    internal companion object {
        internal const val XML_LINK = "link"
    }

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
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                    }
            }
        } ?: Text(defaultTextStyles)
    }

    override val isIgnored get() = super.isIgnored || !isClickable

    @RestrictTo(RestrictTo.Scope.TESTS)
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

    // region HasAnalyticsEvents
    @VisibleForTesting
    internal val analyticsEvents: List<AnalyticsEvent>

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.CLICKED -> analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.DEFAULT) }
        else -> error("The $type trigger type is currently unsupported on Links")
    }
    // endregion HasAnalyticsEvents
}
