package org.cru.godtools.tool.model.lesson

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.EventId
import org.cru.godtools.tool.model.HasAnalyticsEvents
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Multiselect.Companion.XML_MULTISELECT_OPTION_BACKGROUND_COLOR
import org.cru.godtools.tool.model.Multiselect.Companion.XML_MULTISELECT_OPTION_SELECTED_COLOR
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.PlatformColor
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.XMLNS_ANALYTICS
import org.cru.godtools.tool.model.XMLNS_CONTENT
import org.cru.godtools.tool.model.XML_BACKGROUND_COLOR
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.XML_LISTENERS
import org.cru.godtools.tool.model.XML_TEXT_SCALE
import org.cru.godtools.tool.model.color
import org.cru.godtools.tool.model.getResource
import org.cru.godtools.tool.model.lesson.LessonPage.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.tool.model.lesson.LessonPage.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.lesson.LessonPage.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.model.textScale
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.model.toEventIds
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_PAGE = "page"
private const val XML_HIDDEN = "hidden"
private const val XML_CONTENT = "content"

class LessonPage : BaseModel, Parent, Styles, HasAnalyticsEvents {
    internal companion object {
        @AndroidColorInt
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_COLOR = color(0, 0, 0, 0.0)
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X
        @VisibleForTesting
        internal val DEFAULT_BACKGROUND_IMAGE_GRAVITY = ImageGravity.CENTER
    }

    val id by lazy { fileName ?: "${manifest.code}-$position" }
    val position by lazy { manifest.lessonPages.indexOf(this) }

    @VisibleForTesting
    internal val fileName: String?

    val isHidden: Boolean
    val listeners: Set<EventId>

    @VisibleForTesting
    internal val analyticsEvents: List<AnalyticsEvent>

    @AndroidColorInt
    internal val backgroundColor: PlatformColor

    @VisibleForTesting
    internal val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    internal val backgroundImageGravity: ImageGravity
    internal val backgroundImageScaleType: ImageScaleType

    @AndroidColorInt
    private val _controlColor: PlatformColor?
    @get:AndroidColorInt
    internal val controlColor get() = _controlColor ?: manifest.lessonControlColor

    private val _multiselectOptionBackgroundColor: PlatformColor?
    override val multiselectOptionBackgroundColor
        get() = _multiselectOptionBackgroundColor ?: super.multiselectOptionBackgroundColor
    private val _multiselectOptionSelectedColor: PlatformColor?
    override val multiselectOptionSelectedColor
        get() = _multiselectOptionSelectedColor ?: super.multiselectOptionSelectedColor

    private val _textScale: Double
    override val textScale get() = _textScale * stylesParent.textScale

    override val content: List<Content>

    internal constructor(
        manifest: Manifest,
        fileName: String?,
        parser: XmlPullParser
    ) : super(manifest) {
        this.fileName = fileName

        parser.require(XmlPullParser.START_TAG, XMLNS_LESSON, XML_PAGE)

        isHidden = parser.getAttributeValue(XML_HIDDEN)?.toBoolean() ?: false
        listeners = parser.getAttributeValue(XML_LISTENERS)?.toEventIds()?.toSet().orEmpty()

        backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR
        _backgroundImage = parser.getAttributeValue(XML_BACKGROUND_IMAGE)
        backgroundImageGravity = parser.getAttributeValue(XML_BACKGROUND_IMAGE_GRAVITY)?.toImageGravityOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = parser.getAttributeValue(XML_BACKGROUND_IMAGE_SCALE_TYPE)?.toImageScaleTypeOrNull()
            ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _controlColor = parser.getAttributeValue(XML_CONTROL_COLOR)?.toColorOrNull()

        _multiselectOptionBackgroundColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_BACKGROUND_COLOR)?.toColorOrNull()
        _multiselectOptionSelectedColor =
            parser.getAttributeValue(XMLNS_CONTENT, XML_MULTISELECT_OPTION_SELECTED_COLOR)?.toColorOrNull()

        _textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE

        analyticsEvents = mutableListOf()
        val content = mutableListOf<Content>()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }
                XMLNS_LESSON -> when (parser.name) {
                    XML_CONTENT -> content += parseContent(parser)
                }
            }
        }
        this.content = content
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        manifest: Manifest = Manifest(),
        fileName: String? = null,
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        backgroundColor: PlatformColor = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        backgroundImageGravity: ImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        controlColor: PlatformColor? = null,
        textScale: Double = DEFAULT_TEXT_SCALE
    ) : super(manifest) {
        this.fileName = fileName

        isHidden = false
        listeners = emptySet()

        this.analyticsEvents = analyticsEvents

        this.backgroundColor = backgroundColor
        _backgroundImage = backgroundImage
        this.backgroundImageGravity = backgroundImageGravity
        this.backgroundImageScaleType = backgroundImageScaleType

        _controlColor = controlColor

        _multiselectOptionBackgroundColor = null
        _multiselectOptionSelectedColor = null

        _textScale = textScale

        content = emptyList()
    }

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
        Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
        else -> error("Analytics trigger type $type is not currently supported on Heroes")
    }
}

@get:AndroidColorInt
val LessonPage?.backgroundColor get() = this?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR
val LessonPage?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
val LessonPage?.backgroundImageGravity get() = this?.backgroundImageGravity ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY

@get:AndroidColorInt
val LessonPage?.controlColor get() = this?.controlColor ?: DEFAULT_LESSON_CONTROL_COLOR
