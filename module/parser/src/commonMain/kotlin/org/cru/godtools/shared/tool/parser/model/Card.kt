package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_CONTENT_CARD
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

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
