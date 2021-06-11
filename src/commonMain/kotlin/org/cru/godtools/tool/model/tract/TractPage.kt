package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Color
import org.cru.godtools.tool.model.EventId
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.XML_BACKGROUND_COLOR
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.XML_LISTENERS
import org.cru.godtools.tool.model.XML_PRIMARY_COLOR
import org.cru.godtools.tool.model.XML_PRIMARY_TEXT_COLOR
import org.cru.godtools.tool.model.XML_TEXT_COLOR
import org.cru.godtools.tool.model.XML_TEXT_SCALE
import org.cru.godtools.tool.model.color
import org.cru.godtools.tool.model.getResource
import org.cru.godtools.tool.model.primaryColor
import org.cru.godtools.tool.model.primaryTextColor
import org.cru.godtools.tool.model.textColor
import org.cru.godtools.tool.model.textScale
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.model.toEventIds
import org.cru.godtools.tool.model.tract.TractPage.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.tool.model.tract.TractPage.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.tract.TractPage.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_PAGE = "page"
private const val XML_CARD_TEXT_COLOR = "card-text-color"
private const val XML_MODALS = "modals"

class TractPage : BaseModel, Styles {
    internal companion object {
        @AndroidColorInt
        internal val DEFAULT_BACKGROUND_COLOR = color(0, 0, 0, 0.0)
        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = ImageGravity.CENTER

        @VisibleForTesting
        internal const val DEFAULT_TEXT_SCALE = 1.0
    }

    val id get() = fileName ?: "${manifest.code}-$position"
    val position: Int

    @VisibleForTesting
    internal val fileName: String?
    val listeners: Set<EventId>

    @AndroidColorInt
    val backgroundColor: Color
    private val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    val backgroundImageGravity: ImageGravity
    val backgroundImageScaleType: ImageScaleType

    val header: Header?
    val hero: Hero?
    val modals: List<Modal>
    val callToAction: CallToAction

    @AndroidColorInt
    private val _primaryColor: Color?
    @get:AndroidColorInt
    override val primaryColor get() = _primaryColor ?: stylesParent.primaryColor

    @AndroidColorInt
    private val _primaryTextColor: Color?
    @get:AndroidColorInt
    override val primaryTextColor get() = _primaryTextColor ?: stylesParent.primaryTextColor

    @AndroidColorInt
    private val _textColor: Color?
    @get:AndroidColorInt
    override val textColor get() = _textColor ?: stylesParent.textColor
    private val _textScale: Double
    override val textScale get() = _textScale * stylesParent.textScale

    @AndroidColorInt
    private val _cardTextColor: Color?
    @get:AndroidColorInt
    val cardTextColor get() = _cardTextColor ?: textColor

    @AndroidColorInt
    private val _cardBackgroundColor: Color?
    @get:AndroidColorInt
    val cardBackgroundColor get() = _cardBackgroundColor ?: manifest.cardBackgroundColor

    internal constructor(
        manifest: Manifest,
        position: Int,
        fileName: String?,
        parser: XmlPullParser
    ) : super(manifest) {
        this.position = position
        this.fileName = fileName

        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_PAGE)

        listeners = parser.getAttributeValue(XML_LISTENERS).toEventIds().toSet()
        _primaryColor = parser.getAttributeValue(XML_PRIMARY_COLOR)?.toColorOrNull()
        _primaryTextColor = parser.getAttributeValue(XML_PRIMARY_TEXT_COLOR)?.toColorOrNull()

        backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR
        _backgroundImage = parser.getAttributeValue(XML_BACKGROUND_IMAGE)
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toImageGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull()
        _textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE

        _cardBackgroundColor = parser.getAttributeValue(XML_CARD_BACKGROUND_COLOR)?.toColorOrNull()
        _cardTextColor = parser.getAttributeValue(XML_CARD_TEXT_COLOR)?.toColorOrNull()

        // process any child elements
        var header: Header? = null
        var hero: Hero? = null
        modals = mutableListOf()
        var callToAction: CallToAction? = null
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_TRACT -> when (parser.name) {
                    Header.XML_HEADER -> header = Header(this, parser)
                    Hero.XML_HERO -> hero = Hero(this, parser)
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
    internal constructor(
        manifest: Manifest,
        position: Int = 0,
        fileName: String? = null,
        textScale: Double = DEFAULT_TEXT_SCALE,
        cardBackgroundColor: Color? = null,
        callToAction: ((TractPage) -> CallToAction?)? = null
    ) : super(manifest) {
        this.position = position
        this.fileName = fileName

        listeners = emptySet()

        _primaryColor = null
        _primaryTextColor = null

        backgroundColor = DEFAULT_BACKGROUND_COLOR
        _backgroundImage = null
        backgroundImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _textColor = null
        _textScale = textScale

        _cardBackgroundColor = cardBackgroundColor
        _cardTextColor = null

        header = null
        hero = null
        modals = emptyList()
        this.callToAction = callToAction?.invoke(this) ?: CallToAction(this)
    }

    fun findModal(id: String?) = modals.firstOrNull { it.id.equals(id, ignoreCase = true) }

    @OptIn(ExperimentalStdlibApi::class)
    private fun XmlPullParser.parseModalsXml() = buildList {
        require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_MODALS)
        parseChildren {
            when (namespace) {
                XMLNS_TRACT -> when (name) {
                    Modal.XML_MODAL -> add(Modal(this@TractPage, size, this@parseModalsXml))
                }
            }
        }
    }
}

@get:AndroidColorInt
val TractPage?.backgroundColor get() = this?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR
val TractPage?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
val TractPage?.backgroundImageGravity get() = this?.backgroundImageGravity ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
