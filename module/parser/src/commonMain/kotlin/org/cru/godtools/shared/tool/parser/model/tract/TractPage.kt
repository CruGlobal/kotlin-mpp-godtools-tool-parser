package org.cru.godtools.shared.tool.parser.model.tract

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import com.github.ajalt.colormath.Color
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.internal.toColorOrNull
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Base
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.Styles
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.shared.tool.parser.model.XML_DISMISS_LISTENERS
import org.cru.godtools.shared.tool.parser.model.XML_LISTENERS
import org.cru.godtools.shared.tool.parser.model.XML_TEXT_COLOR
import org.cru.godtools.shared.tool.parser.model.backgroundColor
import org.cru.godtools.shared.tool.parser.model.contentTips
import org.cru.godtools.shared.tool.parser.model.getResource
import org.cru.godtools.shared.tool.parser.model.page.Page
import org.cru.godtools.shared.tool.parser.model.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.model.parseTextChild
import org.cru.godtools.shared.tool.parser.model.stylesOverride
import org.cru.godtools.shared.tool.parser.model.toEventIds
import org.cru.godtools.shared.tool.parser.model.tract.TractPage.Card
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_CARD_TEXT_COLOR = "card-text-color"
private const val XML_CARDS = "cards"
private const val XML_MODALS = "modals"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class TractPage : Page {
    val isLastPage get() = manifest.pages.lastOrNull() == this

    override val analyticsEvents = emptyList<AnalyticsEvent>()

    val header: Header?
    val hero: Hero?
    @JsExport.Ignore
    @JsName("_modals")
    val modals: List<Modal>
    val callToAction: CallToAction

    internal constructor(
        container: HasPages,
        fileName: String?,
        parser: XmlPullParser
    ) : super(container, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_PAGE)

        _cardTextColor = parser.getAttributeValue(XML_CARD_TEXT_COLOR)?.toColorOrNull()

        // process any child elements
        var header: Header? = null
        var hero: Hero? = null
        cards = mutableListOf()
        modals = mutableListOf()
        var callToAction: CallToAction? = null
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_TRACT -> when (parser.name) {
                    Header.XML_HEADER -> header = Header(this, parser)
                    Hero.XML_HERO -> hero = Hero(this, parser)
                    XML_CARDS -> cards += parser.parseCardsXml()
                    XML_MODALS -> modals += parser.parseModalsXml()
                    CallToAction.XML_CALL_TO_ACTION -> callToAction = CallToAction(this, parser)
                }
            }
        }
        this.header = header
        this.hero = hero
        this.callToAction = callToAction ?: CallToAction(this)
    }

    @JsName("createTestTractPage")
    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
        manifest: Manifest = Manifest(),
        fileName: String? = null,
        backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        primaryColor: Color? = null,
        backgroundImageGravity: Gravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        textColor: Color? = null,
        textScale: Double = DEFAULT_TEXT_SCALE,
        cardBackgroundColor: Color? = null,
        cardTextColor: Color? = null,
        cards: ((TractPage) -> List<Card>?)? = null,
        callToAction: ((TractPage) -> CallToAction?)? = null
    ) : super(
        manifest,
        fileName = fileName,
        primaryColor = primaryColor,
        backgroundColor = backgroundColor,
        backgroundImage = backgroundImage,
        backgroundImageGravity = backgroundImageGravity,
        backgroundImageScaleType = backgroundImageScaleType,
        cardBackgroundColor = cardBackgroundColor,
        textColor = textColor,
        textScale = textScale
    ) {
        _cardTextColor = cardTextColor

        header = null
        hero = null
        this.cards = cards?.invoke(this).orEmpty()
        modals = emptyList()
        this.callToAction = callToAction?.invoke(this) ?: CallToAction(this)
    }

    fun findModal(id: String?) = modals.firstOrNull { it.id.equals(id, ignoreCase = true) }

    // region Cards
    @JsExport.Ignore
    @JsName("_cards")
    val cards: List<Card>
    val visibleCards get() = cards.filter { !it.isHidden }

    private val _cardTextColor: Color?
    internal val cardTextColor get() = _cardTextColor ?: textColor

    class Card : BaseModel, Styles, Parent, HasAnalyticsEvents {
        internal companion object {
            internal const val XML_CARD = "card"
            private const val XML_LABEL = "label"
            private const val XML_HIDDEN = "hidden"

            internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X
            internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = Gravity.CENTER
        }

        val page: TractPage

        val id get() = "${page.id}-$position"
        val position: Int
        val visiblePosition get() = page.visibleCards.indexOf(this).takeUnless { it == -1 }
        val isLastVisibleCard get() = this == page.visibleCards.lastOrNull()

        val isHidden: Boolean

        @JsExport.Ignore
        @JsName("_listeners")
        val listeners: Set<EventId>
        @JsExport.Ignore
        @JsName("_dismissListeners")
        val dismissListeners: Set<EventId>

        @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
        private val _backgroundColor: Color?
        internal val backgroundColor get() = _backgroundColor ?: page.cardBackgroundColor
        private val _backgroundImage: String?
        val backgroundImage get() = getResource(_backgroundImage)
        internal val backgroundImageGravity: Gravity
        internal val backgroundImageScaleType: ImageScaleType

        private val _textColor: Color?
        override val textColor get() = _textColor ?: page.cardTextColor

        private val labelParent by lazy { stylesOverride(textColor = { primaryColor }) }
        val label: Text?
        override val content: List<Content>
        val tips get() = contentTips

        internal constructor(page: TractPage, position: Int, parser: XmlPullParser) : super(page) {
            this.page = page
            this.position = position

            parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_CARD)

            isHidden = parser.getAttributeValue(XML_HIDDEN)?.toBoolean() ?: false
            listeners = parser.getAttributeValue(XML_LISTENERS)?.toEventIds()?.toSet().orEmpty()
            dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS)?.toEventIds()?.toSet().orEmpty()

            _backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull()
            _backgroundImage = parser.getAttributeValue(XML_BACKGROUND_IMAGE)
            backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toGravityOrNull()
                ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
            backgroundImageScaleType =
                parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
                    ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

            _textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull()

            // process any child elements
            analyticsEvents = mutableListOf()
            var label: Text? = null
            content = parseContent(parser) {
                when (parser.namespace) {
                    XMLNS_ANALYTICS -> when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                    }

                    XMLNS_TRACT -> when (parser.name) {
                        XML_LABEL -> label = parser.parseTextChild(labelParent, XMLNS_TRACT, XML_LABEL)
                    }
                }
            }
            this.label = label
        }

        @JsName("createTestCard")
        @RestrictTo(RestrictTo.Scope.TESTS)
        constructor(
            page: TractPage = TractPage(),
            position: Int = 0,
            backgroundColor: Color? = null,
            backgroundImage: String? = null,
            backgroundImageGravity: Gravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
            backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
            isHidden: Boolean = false,
            analyticsEvents: List<AnalyticsEvent> = emptyList(),
            label: ((Base) -> Text?)? = null,
            content: ((Card) -> List<Content>?)? = null
        ) : super(page) {
            this.page = page
            this.position = position

            this.isHidden = isHidden
            listeners = emptySet()
            dismissListeners = emptySet()
            this.analyticsEvents = analyticsEvents

            _backgroundColor = backgroundColor
            _backgroundImage = backgroundImage
            this.backgroundImageGravity = backgroundImageGravity
            this.backgroundImageScaleType = backgroundImageScaleType

            _textColor = null

            this.label = label?.invoke(labelParent)
            this.content = content?.invoke(this).orEmpty()
        }

        // region HasAnalyticsEvents
        @VisibleForTesting
        internal val analyticsEvents: List<AnalyticsEvent>

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
            Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
            else -> error("Analytics trigger type $type is not currently supported on Cards")
        }
        // endregion HasAnalyticsEvents

        // region Kotlin/JS interop
        @HiddenFromObjC
        @JsName("dismissListeners")
        val jsDismissListeners get() = dismissListeners.toTypedArray()

        @HiddenFromObjC
        @JsName("listeners")
        val jsListeners get() = listeners.toTypedArray()
        // endregion Kotlin/JS interop
    }

    private fun XmlPullParser.parseCardsXml() = buildList {
        require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_CARDS)
        parseChildren {
            when (namespace) {
                XMLNS_TRACT -> when (name) {
                    Card.XML_CARD -> add(Card(this@TractPage, size, this@parseCardsXml))
                }
            }
        }
    }
    // endregion Cards

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("cards")
    val jsCards get() = cards.toTypedArray()

    @HiddenFromObjC
    @JsName("modals")
    val jsModals get() = modals.toTypedArray()
    // endregion Kotlin/JS interop

    private fun XmlPullParser.parseModalsXml() = buildList {
        require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_MODALS)
        parseChildren {
            when (namespace) {
                XMLNS_TRACT -> when (name) {
                    Modal.XML_MODAL -> add(Modal(this@TractPage, this@parseModalsXml))
                }
            }
        }
    }
}

@get:AndroidColorInt
val Card?.backgroundColor get() = this?.backgroundColor ?: this?.manifest.backgroundColor
val Card?.backgroundImageGravity get() = this?.backgroundImageGravity ?: Card.DEFAULT_BACKGROUND_IMAGE_GRAVITY
val Card?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: Card.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
