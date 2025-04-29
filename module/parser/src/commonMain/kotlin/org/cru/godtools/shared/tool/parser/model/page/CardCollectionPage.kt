package org.cru.godtools.shared.tool.parser.model.page

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
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
import org.cru.godtools.shared.tool.parser.model.Styles
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.contentTips
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage.Card.Companion.XML_CARD
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.model.toColorOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_CARDS = "cards"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class CardCollectionPage : Page {
    internal companion object {
        const val TYPE_CARD_COLLECTION = "cardcollection"
    }

    override val analyticsEvents: List<AnalyticsEvent>
    val header: Header?
    @JsExport.Ignore
    @JsName("_cards")
    val cards: List<Card>

    internal constructor(
        container: HasPages,
        fileName: String?,
        parser: XmlPullParser
    ) : super(container, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGE)
        parser.requirePageType(TYPE_CARD_COLLECTION)

        analyticsEvents = mutableListOf()
        var header: Header? = null
        cards = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }

                XMLNS_PAGE -> when (parser.name) {
                    Header.XML_HEADER -> header = Header(this, parser)
                    XML_CARDS -> cards += parseCards(parser)
                }
            }
        }
        this.header = header
    }

    private fun parseCards(parser: XmlPullParser) = buildList {
        parser.parseChildren {
            if (parser.namespace == XMLNS_PAGE && parser.name == XML_CARD) {
                add(Card(this@CardCollectionPage, parser))
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        manifest: Manifest = Manifest(),
        id: String? = null,
        parentPage: String? = null,
    ) : super(manifest, id = id, parentPage = parentPage) {
        analyticsEvents = emptyList()
        header = null
        cards = emptyList()
    }

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("cards")
    val jsCards get() = cards.toTypedArray()
    // endregion Kotlin/JS interop

    class Header : BaseModel, Parent, Styles {
        internal companion object {
            internal const val XML_HEADER = "header"

            val DEFAULT_TEXT_ALIGN = Text.Align.CENTER
        }

        // region Text Styles
        override val textAlign = DEFAULT_TEXT_ALIGN
        // endregion Text Styles

        override val content: List<Content>

        internal constructor(page: CardCollectionPage, parser: XmlPullParser) : super(page) {
            parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_HEADER)
            content = parseContent(parser)
        }

        @RestrictTo(RestrictTo.Scope.TESTS)
        internal constructor(
            page: CardCollectionPage = CardCollectionPage(),
            content: (Header) -> List<Content> = { emptyList() }
        ) : super(page) {
            this.content = content(this)
        }
    }

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
        @JsExport.Ignore
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

        @RestrictTo(RestrictTo.Scope.TESTS)
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
