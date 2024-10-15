package org.cru.godtools.shared.tool.parser.model.page

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.PlatformColor
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.contentTips
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage.Card.Companion.XML_CARD
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.model.toColorOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_CARDS = "cards"

class CardCollectionPage : Page {
    internal companion object {
        const val TYPE_CARD_COLLECTION = "cardcollection"
    }

    override val analyticsEvents: List<AnalyticsEvent>
    val cards: List<Card>

    internal constructor(
        container: HasPages,
        fileName: String?,
        parser: XmlPullParser
    ) : super(container, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGE)
        parser.requirePageType(TYPE_CARD_COLLECTION)

        analyticsEvents = mutableListOf()
        cards = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }

                XMLNS_PAGE -> when (parser.name) {
                    XML_CARDS -> cards += parseCards(parser)
                }
            }
        }
    }

    private fun parseCards(parser: XmlPullParser) = buildList {
        parser.parseChildren {
            if (parser.namespace == XMLNS_PAGE && parser.name == XML_CARD) {
                add(Card(this@CardCollectionPage, parser))
            }
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        manifest: Manifest,
        id: String?,
        parentPage: String? = null,
    ) : super(manifest, id = id, parentPage = parentPage) {
        analyticsEvents = emptyList()
        cards = emptyList()
    }

    override fun supports(type: Manifest.Type) = type == Manifest.Type.CYOA

    class Card : BaseModel, Parent, HasAnalyticsEvents {
        internal companion object {
            internal const val XML_CARD = "card"
            private const val XML_ID = "id"
            private const val XML_CONTENT = "content"
        }

        val page: CardCollectionPage

        private val _id: String?
        val id get() = _id ?: "${page.id}-$position"
        val position get() = page.cards.indexOf(this)

        @AndroidColorInt
        @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
        private val _backgroundColor: PlatformColor?
        @get:AndroidColorInt
        internal val backgroundColor get() = _backgroundColor ?: page.cardBackgroundColor

        private val analyticsEvents: List<AnalyticsEvent>
        override val content: List<Content>
        val tips get() = contentTips

        internal constructor(page: CardCollectionPage, parser: XmlPullParser) : super(page) {
            this.page = page
            parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_CARD)

            _id = parser.getAttributeValue(XML_ID)
            _backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull()

            // process any child elements
            analyticsEvents = mutableListOf()
            content = mutableListOf()
            parser.parseChildren {
                when (parser.namespace) {
                    XMLNS_ANALYTICS -> when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                    }

                    XMLNS_PAGE -> when (parser.name) {
                        XML_CONTENT -> content += parseContent(parser)
                    }
                }
            }
        }

        @RestrictTo(RestrictToScope.TESTS)
        internal constructor(
            page: CardCollectionPage,
            id: String?,
        ) : super(page) {
            this.page = page

            _id = id
            _backgroundColor = null

            analyticsEvents = emptyList()
            content = emptyList()
        }

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
            Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
            else -> error("Analytics trigger type $type is not currently supported on CardCollection Cards")
        }
    }
}
