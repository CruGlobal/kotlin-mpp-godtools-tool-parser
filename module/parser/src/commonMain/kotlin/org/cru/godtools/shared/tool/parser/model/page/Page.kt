package org.cru.godtools.shared.tool.parser.model.page

import io.github.aakira.napier.Napier
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Card
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.model.HasAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Multiselect.Companion.XML_MULTISELECT_OPTION_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.Multiselect.Companion.XML_MULTISELECT_OPTION_SELECTED_COLOR
import org.cru.godtools.shared.tool.parser.model.PlatformColor
import org.cru.godtools.shared.tool.parser.model.Styles
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.XMLNS_CONTENT
import org.cru.godtools.shared.tool.parser.model.XMLNS_CYOA
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.shared.tool.parser.model.XML_DISMISS_LISTENERS
import org.cru.godtools.shared.tool.parser.model.XML_LISTENERS
import org.cru.godtools.shared.tool.parser.model.XML_PRIMARY_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_PRIMARY_TEXT_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_TEXT_COLOR
import org.cru.godtools.shared.tool.parser.model.XML_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.color
import org.cru.godtools.shared.tool.parser.model.getResource
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.lesson.XMLNS_LESSON
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage.Companion.TYPE_CARD_COLLECTION
import org.cru.godtools.shared.tool.parser.model.page.ContentPage.Companion.TYPE_CONTENT
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.primaryTextColor
import org.cru.godtools.shared.tool.parser.model.textColor
import org.cru.godtools.shared.tool.parser.model.textScale
import org.cru.godtools.shared.tool.parser.model.toColorOrNull
import org.cru.godtools.shared.tool.parser.model.toEventIds
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.XMLNS_TRACT
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException

private const val XML_TYPE = "type"
private const val XML_ID = "id"
private const val XML_PARENT = "parent"
private const val XML_HIDDEN = "hidden"

abstract class Page : BaseModel, Styles, HasAnalyticsEvents {
    internal companion object {
        internal const val XML_PAGE = "page"

        @AndroidColorInt
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_COLOR = color(0, 0, 0, 0.0)
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = Gravity.CENTER
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X

        fun parse(manifest: Manifest, fileName: String?, parser: XmlPullParser): Page? {
            parser.require(XmlPullParser.START_TAG, null, XML_PAGE)

            return when (parser.namespace) {
                XMLNS_LESSON -> LessonPage(manifest, fileName, parser)
                XMLNS_TRACT -> TractPage(manifest, fileName, parser)
                XMLNS_PAGE -> {
                    when (val type = parser.getAttributeValue(XMLNS_XSI, XML_TYPE)) {
                        TYPE_CARD_COLLECTION -> CardCollectionPage(manifest, fileName, parser)
                        TYPE_CONTENT -> ContentPage(manifest, fileName, parser)
                        else -> {
                            val message = "Unrecognized page type: <${parser.namespace}:${parser.name} type=$type>"
                            Napier.e(message, UnsupportedOperationException(message), "Page")
                            null
                        }
                    }
                }
                else -> {
                    val message = "Unrecognized page namespace: ${parser.namespace}"
                    Napier.e(message, UnsupportedOperationException(message), "Page")
                    null
                }
            }?.takeIf { it.supports(manifest.type) }
        }

        internal fun XmlPullParser.requirePageType(type: String) {
            val actual = getAttributeValue(XMLNS_XSI, XML_TYPE)
            if (type != actual) throw XmlPullParserException("expected $type not $actual")
        }
    }

    val id by lazy { _id ?: fileName ?: "${manifest.code}-$position" }
    val position by lazy { manifest.pages.indexOf(this) }

    private val _id: String?
    @VisibleForTesting
    internal val fileName: String?

    private val _parentPage: String?
    val parentPage get() = manifest.findPage(_parentPage)

    val isHidden: Boolean

    val listeners: Set<EventId>
    val dismissListeners: Set<EventId>

    @AndroidColorInt
    private val _primaryColor: PlatformColor?
    @get:AndroidColorInt
    override val primaryColor get() = _primaryColor ?: stylesParent.primaryColor

    @AndroidColorInt
    private val _primaryTextColor: PlatformColor?
    @get:AndroidColorInt
    override val primaryTextColor get() = _primaryTextColor ?: stylesParent.primaryTextColor

    @AndroidColorInt
    internal val backgroundColor: PlatformColor

    @VisibleForTesting
    internal val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    internal val backgroundImageGravity: Gravity
    internal val backgroundImageScaleType: ImageScaleType

    @AndroidColorInt
    private val _controlColor: PlatformColor?
    @get:AndroidColorInt
    internal val controlColor get() = _controlColor ?: manifest.pageControlColor

    @AndroidColorInt
    private val _cardBackgroundColor: PlatformColor?
    @get:AndroidColorInt
    override val cardBackgroundColor get() = _cardBackgroundColor ?: manifest.cardBackgroundColor

    private val _multiselectOptionBackgroundColor: PlatformColor?
    override val multiselectOptionBackgroundColor
        get() = _multiselectOptionBackgroundColor ?: super.multiselectOptionBackgroundColor
    private val _multiselectOptionSelectedColor: PlatformColor?
    override val multiselectOptionSelectedColor
        get() = _multiselectOptionSelectedColor ?: super.multiselectOptionSelectedColor

    @AndroidColorInt
    private val _textColor: PlatformColor?
    @get:AndroidColorInt
    override val textColor get() = _textColor ?: stylesParent.textColor
    private val _textScale: Double
    override val textScale get() = _textScale * stylesParent.textScale

    internal constructor(manifest: Manifest, fileName: String?, parser: XmlPullParser) : super(manifest) {
        parser.require(XmlPullParser.START_TAG, null, XML_PAGE)

        _id = parser.getAttributeValue(XML_ID)
        this.fileName = fileName
        _parentPage = parser.getAttributeValue(XMLNS_CYOA, XML_PARENT)

        isHidden = parser.getAttributeValue(XML_HIDDEN)?.toBoolean() ?: false

        listeners = parser.getAttributeValue(XML_LISTENERS).toEventIds().toSet()
        dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS).toEventIds().toSet()

        _primaryColor = parser.getAttributeValue(XML_PRIMARY_COLOR)?.toColorOrNull()
        _primaryTextColor = parser.getAttributeValue(XML_PRIMARY_TEXT_COLOR)?.toColorOrNull()

        backgroundColor =
            parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR
        _backgroundImage = parser.getAttributeValue(XML_BACKGROUND_IMAGE)
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _controlColor = parser.getAttributeValue(XML_CONTROL_COLOR)?.toColorOrNull()

        _cardBackgroundColor = parser.getAttributeValue(XMLNS_CONTENT, Card.XML_CARD_BACKGROUND_COLOR)?.toColorOrNull()

        _multiselectOptionBackgroundColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_BACKGROUND_COLOR)?.toColorOrNull()
        _multiselectOptionSelectedColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_SELECTED_COLOR)?.toColorOrNull()

        _textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull()
        _textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE
    }

    @RestrictTo(RestrictToScope.SUBCLASSES, RestrictToScope.TESTS)
    internal constructor(
        manifest: Manifest = Manifest(),
        id: String? = null,
        fileName: String? = null,
        parentPage: String? = null,
        primaryColor: PlatformColor? = null,
        backgroundColor: PlatformColor = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        backgroundImageGravity: Gravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        controlColor: PlatformColor? = null,
        cardBackgroundColor: PlatformColor? = null,
        textColor: PlatformColor? = null,
        textScale: Double = DEFAULT_TEXT_SCALE
    ) : super(manifest) {
        _id = id
        this.fileName = fileName
        _parentPage = parentPage

        isHidden = false

        listeners = emptySet()
        dismissListeners = emptySet()

        _primaryColor = primaryColor
        _primaryTextColor = null

        this.backgroundColor = backgroundColor
        _backgroundImage = backgroundImage
        this.backgroundImageGravity = backgroundImageGravity
        this.backgroundImageScaleType = backgroundImageScaleType

        _controlColor = controlColor

        _cardBackgroundColor = cardBackgroundColor

        _multiselectOptionBackgroundColor = null
        _multiselectOptionSelectedColor = null

        _textColor = textColor
        _textScale = textScale
    }

    internal abstract fun supports(type: Manifest.Type): Boolean

    // region HasAnalyticsEvents
    @VisibleForTesting
    internal abstract val analyticsEvents: List<AnalyticsEvent>

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
        Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
        else -> error("Analytics trigger type $type is not currently supported on Pages")
    }
    // endregion HasAnalyticsEvents
}

@get:AndroidColorInt
val Page?.backgroundColor get() = this?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR
val Page?.backgroundImageGravity get() = this?.backgroundImageGravity ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
val Page?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

@get:AndroidColorInt
val Page?.controlColor get() = this?.controlColor ?: DEFAULT_CONTROL_COLOR
