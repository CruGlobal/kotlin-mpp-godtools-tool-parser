package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.PlatformColor
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.backgroundColor
import org.cru.godtools.tool.model.manifest
import org.cru.godtools.tool.model.page.Page
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

    @RestrictTo(RestrictToScope.TESTS)
    constructor(
        manifest: Manifest = Manifest(),
        fileName: String? = null,
        backgroundColor: PlatformColor = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        primaryColor: PlatformColor? = null,
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
        primaryColor = primaryColor,
        backgroundColor = backgroundColor,
        backgroundImage = backgroundImage,
        backgroundImageGravity = backgroundImageGravity,
        backgroundImageScaleType = backgroundImageScaleType,
        textColor = textColor,
        textScale = textScale
    ) {
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

@get:AndroidColorInt
val Card?.backgroundColor get() = this?.backgroundColor ?: manifest.backgroundColor
val Card?.backgroundImageGravity get() = this?.backgroundImageGravity ?: Card.DEFAULT_BACKGROUND_IMAGE_GRAVITY
val Card?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: Card.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
