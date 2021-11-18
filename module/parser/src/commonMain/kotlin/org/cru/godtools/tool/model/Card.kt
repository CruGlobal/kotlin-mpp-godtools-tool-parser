package org.cru.godtools.tool.model

import org.cru.godtools.tool.FEATURE_CONTENT_CARD
import org.cru.godtools.tool.ParserConfig
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

        parser.parseClickableAttrs { events, url ->
            this.events = events
            this.url = url
        }

        content = parseContent(parser)
    }

    override val isIgnored get() = FEATURE_CONTENT_CARD !in ParserConfig.supportedFeatures || super.isIgnored
}

val Card?.backgroundColor get() = this?._backgroundColor ?: stylesParent.cardBackgroundColor
