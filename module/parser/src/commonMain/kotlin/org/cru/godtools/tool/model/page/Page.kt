package org.cru.godtools.tool.model.page

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.EventId
import org.cru.godtools.tool.model.HasAnalyticsEvents
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Multiselect.Companion.XML_MULTISELECT_OPTION_BACKGROUND_COLOR
import org.cru.godtools.tool.model.Multiselect.Companion.XML_MULTISELECT_OPTION_SELECTED_COLOR
import org.cru.godtools.tool.model.PlatformColor
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.XMLNS_CONTENT
import org.cru.godtools.tool.model.XML_BACKGROUND_COLOR
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.XML_DISMISS_LISTENERS
import org.cru.godtools.tool.model.XML_LISTENERS
import org.cru.godtools.tool.model.XML_TEXT_SCALE
import org.cru.godtools.tool.model.color
import org.cru.godtools.tool.model.getResource
import org.cru.godtools.tool.model.lesson.LessonPage
import org.cru.godtools.tool.model.lesson.XMLNS_LESSON
import org.cru.godtools.tool.model.textScale
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.model.toEventIds
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.XmlPullParserException

private const val XML_PAGE = "page"
private const val XML_TYPE = "type"

abstract class Page : BaseModel, Styles, HasAnalyticsEvents {
    internal companion object {
        @AndroidColorInt
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_COLOR = color(0, 0, 0, 0.0)
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = ImageGravity.CENTER

        fun parse(manifest: Manifest, fileName: String?, parser: XmlPullParser): Page? {
            parser.require(XmlPullParser.START_TAG, null, XML_PAGE)

            return when (parser.namespace) {
                XMLNS_LESSON -> LessonPage(manifest, fileName, parser)
                XMLNS_PAGE -> {
                    when (val type = parser.getAttributeValue(XMLNS_XSI, XML_TYPE)) {
                        else -> {
                            val message = "Unrecognized page type: <${parser.namespace}:${parser.name} type=$type>"
                            Napier.e(message, UnsupportedOperationException(message), "Page")
                            null
                        }
                    }
                }
                else -> throw XmlPullParserException("Unrecognized page namespace: ${parser.namespace}")
            }
        }
    }

    val listeners: Set<EventId>
    val dismissListeners: Set<EventId>

    @AndroidColorInt
    internal val backgroundColor: PlatformColor

    @VisibleForTesting
    internal val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    internal val backgroundImageGravity: ImageGravity
    internal val backgroundImageScaleType: ImageScaleType

    private val _multiselectOptionBackgroundColor: PlatformColor?
    override val multiselectOptionBackgroundColor
        get() = _multiselectOptionBackgroundColor ?: super.multiselectOptionBackgroundColor
    private val _multiselectOptionSelectedColor: PlatformColor?
    override val multiselectOptionSelectedColor
        get() = _multiselectOptionSelectedColor ?: super.multiselectOptionSelectedColor

    private val _textScale: Double
    override val textScale get() = _textScale * stylesParent.textScale

    internal constructor(manifest: Manifest, parser: XmlPullParser) : super(manifest) {
        parser.require(XmlPullParser.START_TAG, null, XML_PAGE)

        listeners = parser.getAttributeValue(XML_LISTENERS).toEventIds().toSet()
        dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS).toEventIds().toSet()

        backgroundColor =
            parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR
        _backgroundImage = parser.getAttributeValue(XML_BACKGROUND_IMAGE)
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toImageGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _multiselectOptionBackgroundColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_BACKGROUND_COLOR)?.toColorOrNull()
        _multiselectOptionSelectedColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_SELECTED_COLOR)?.toColorOrNull()

        _textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES, RestrictTo.Scope.TESTS)
    internal constructor(
        manifest: Manifest,
        backgroundColor: PlatformColor = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        backgroundImageGravity: ImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        textScale: Double = DEFAULT_TEXT_SCALE
    ) : super(manifest) {
        listeners = emptySet()
        dismissListeners = emptySet()

        this.backgroundColor = backgroundColor
        _backgroundImage = backgroundImage
        this.backgroundImageGravity = backgroundImageGravity
        this.backgroundImageScaleType = backgroundImageScaleType

        _multiselectOptionBackgroundColor = null
        _multiselectOptionSelectedColor = null

        _textScale = textScale
    }

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
