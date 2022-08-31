package org.cru.godtools.tool.model

import org.cru.godtools.tool.FEATURE_CONTENT_CARD
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.xml.XmlPullParser

class Card : Content, Parent, Clickable {
    internal companion object {
        internal const val XML_CARD = "card"

        internal const val XML_CARD_BACKGROUND_COLOR = "card-background-color"
    }

    internal val _backgroundColor: PlatformColor?

    override val content: List<Content>

    override val events: List<EventId>
    override val url: Uri?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_CARD)

        _backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR).toColorOrNull()

        parseClickableAttrs(parser) { events, url ->
            this.events = events
            this.url = url
        }

        content = parseContent(parser)
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(parent: Base = Manifest(), backgroundColor: PlatformColor? = null) : super(parent) {
        _backgroundColor = backgroundColor
        content = emptyList()
        events = emptyList()
        url = null
    }

    override val isIgnored get() = !manifest.config.supportsFeature(FEATURE_CONTENT_CARD) || super.isIgnored
}

val Card?.backgroundColor get() = this?._backgroundColor ?: stylesParent.cardBackgroundColor
