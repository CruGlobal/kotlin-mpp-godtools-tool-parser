package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Color
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.EventId
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.XMLNS_ANALYTICS
import org.cru.godtools.tool.model.XML_BACKGROUND_COLOR
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.XML_DISMISS_LISTENERS
import org.cru.godtools.tool.model.XML_LISTENERS
import org.cru.godtools.tool.model.XML_TEXT_COLOR
import org.cru.godtools.tool.model.backgroundColor
import org.cru.godtools.tool.model.contentTips
import org.cru.godtools.tool.model.getResource
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.model.toEventIds
import org.cru.godtools.tool.model.tract.Card.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.tract.Card.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_LABEL = "label"
private const val XML_HIDDEN = "hidden"

class Card : BaseModel, Styles, Parent {
    internal companion object {
        internal const val XML_CARD = "card"

        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = ImageGravity.CENTER
    }

    val page: TractPage

    val id get() = "${page.id}-$position"
    val position: Int
    val visiblePosition get() = page.visibleCards.indexOf(this).takeUnless { it == -1 }
    val isLastVisibleCard get() = this == page.visibleCards.lastOrNull()

    val isHidden: Boolean
    val listeners: Set<EventId>
    val dismissListeners: Set<EventId>
    val analyticsEvents: Collection<AnalyticsEvent>

    @AndroidColorInt
    private val _backgroundColor: Color?
    @get:AndroidColorInt
    internal val backgroundColor get() = _backgroundColor ?: page.cardBackgroundColor
    private val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    internal val backgroundImageGravity: ImageGravity
    val backgroundImageScaleType: ImageScaleType

    @AndroidColorInt
    private val _textColor: Color?
    @get:AndroidColorInt
    override val textColor get() = _textColor ?: page.cardTextColor

    val label: Text?
    override val content: List<Content>
    val tips get() = contentTips

    internal constructor(parent: TractPage, position: Int, parser: XmlPullParser) : super(parent) {
        page = parent
        this.position = position

        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_CARD)

        isHidden = parser.getAttributeValue(null, XML_HIDDEN)?.toBoolean() ?: false
        listeners = parser.getAttributeValue(XML_LISTENERS).toEventIds().toSet()
        dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS).toEventIds().toSet()

        _backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull()
        _backgroundImage = parser.getAttributeValue(XML_BACKGROUND_IMAGE)
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toImageGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull()

        // process any child elements
        analyticsEvents = mutableListOf()
        var label: Text? = null
        content = parseContent(parser) {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }
                XMLNS_TRACT -> when (parser.name) {
                    XML_LABEL -> label = parser.parseTextChild(this@Card, XMLNS_TRACT, XML_LABEL)
                }
            }
        }
        this.label = label
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        parent: TractPage,
        backgroundColor: Color? = null,
        isHidden: Boolean = false,
        content: ((Card) -> List<Content>?)? = null
    ) : super(parent) {
        page = parent
        position = 0

        this.isHidden = isHidden
        listeners = emptySet()
        dismissListeners = emptySet()
        analyticsEvents = emptySet()

        _backgroundColor = backgroundColor
        _backgroundImage = null
        backgroundImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _textColor = null

        label = null
        this.content = content?.invoke(this).orEmpty()
    }
}

@get:AndroidColorInt
val Card?.backgroundColor get() = this?.backgroundColor ?: this?.manifest.backgroundColor
val Card?.backgroundImageGravity get() = this?.backgroundImageGravity ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
val Card?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
