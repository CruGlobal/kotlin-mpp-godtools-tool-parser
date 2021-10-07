package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.PlatformColor
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.XML_PRIMARY_COLOR
import org.cru.godtools.tool.model.XML_PRIMARY_TEXT_COLOR
import org.cru.godtools.tool.model.XML_TEXT_COLOR
import org.cru.godtools.tool.model.page.Page
import org.cru.godtools.tool.model.primaryColor
import org.cru.godtools.tool.model.primaryTextColor
import org.cru.godtools.tool.model.textColor
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_CARD_TEXT_COLOR = "card-text-color"
private const val XML_CARDS = "cards"
private const val XML_MODALS = "modals"

class TractPage : Page, Styles {
    val isLastPage get() = manifest.pages.lastOrNull() == this

    override val analyticsEvents = emptyList<AnalyticsEvent>()

    val header: Header?
    val hero: Hero?
    val cards: List<Card>
    val visibleCards get() = cards.filter { !it.isHidden }
    val modals: List<Modal>
    val callToAction: CallToAction

    @AndroidColorInt
    private val _primaryColor: PlatformColor?
    @get:AndroidColorInt
    override val primaryColor get() = _primaryColor ?: stylesParent.primaryColor

    @AndroidColorInt
    private val _primaryTextColor: PlatformColor?
    @get:AndroidColorInt
    override val primaryTextColor get() = _primaryTextColor ?: stylesParent.primaryTextColor

    @AndroidColorInt
    private val _textColor: PlatformColor?
    @get:AndroidColorInt
    override val textColor get() = _textColor ?: stylesParent.textColor

    @AndroidColorInt
    private val _cardTextColor: PlatformColor?
    @get:AndroidColorInt
    val cardTextColor get() = _cardTextColor ?: textColor

    @AndroidColorInt
    private val _cardBackgroundColor: PlatformColor?
    @get:AndroidColorInt
    val cardBackgroundColor get() = _cardBackgroundColor ?: manifest.cardBackgroundColor

    internal constructor(
        manifest: Manifest,
        fileName: String?,
        parser: XmlPullParser
    ) : super(manifest, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_PAGE)

        _primaryColor = parser.getAttributeValue(XML_PRIMARY_COLOR)?.toColorOrNull()
        _primaryTextColor = parser.getAttributeValue(XML_PRIMARY_TEXT_COLOR)?.toColorOrNull()

        _textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull()

        _cardBackgroundColor = parser.getAttributeValue(XML_CARD_BACKGROUND_COLOR)?.toColorOrNull()
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

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
        manifest: Manifest = Manifest(),
        fileName: String? = null,
        backgroundColor: PlatformColor = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        @AndroidColorInt primaryColor: PlatformColor? = null,
        backgroundImageGravity: ImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        textColor: PlatformColor? = null,
        textScale: Double = DEFAULT_TEXT_SCALE,
        cardBackgroundColor: PlatformColor? = null,
        cardTextColor: PlatformColor? = null,
        cards: ((TractPage) -> List<Card>?)? = null,
        callToAction: ((TractPage) -> CallToAction?)? = null
    ) : super(
        manifest,
        fileName = fileName,
        backgroundColor = backgroundColor,
        backgroundImage = backgroundImage,
        backgroundImageGravity = backgroundImageGravity,
        backgroundImageScaleType = backgroundImageScaleType,
        textScale = textScale
    ) {
        _primaryColor = primaryColor
        _primaryTextColor = null

        _textColor = textColor

        _cardBackgroundColor = cardBackgroundColor
        _cardTextColor = cardTextColor

        header = null
        hero = null
        this.cards = cards?.invoke(this).orEmpty()
        modals = emptyList()
        this.callToAction = callToAction?.invoke(this) ?: CallToAction(this)
    }

    override fun supports(type: Manifest.Type) = type == Manifest.Type.TRACT

    fun findModal(id: String?) = modals.firstOrNull { it.id.equals(id, ignoreCase = true) }

    @OptIn(ExperimentalStdlibApi::class)
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

    @OptIn(ExperimentalStdlibApi::class)
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
