package org.cru.godtools.tool.model.lesson

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Color
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.EventId
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.ImageScaleType.Companion.toImageScaleTypeOrNull
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.XML_BACKGROUND_COLOR
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.XML_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.XML_LISTENERS
import org.cru.godtools.tool.model.XML_TEXT_SCALE
import org.cru.godtools.tool.model.color
import org.cru.godtools.tool.model.getResource
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.model.textScale
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.model.toEventIds
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren
import kotlin.native.concurrent.SharedImmutable

private const val XML_PAGE = "page"
private const val XML_HIDDEN = "hidden"
private const val XML_CONTENT = "content"

@AndroidColorInt
@SharedImmutable
private val DEFAULT_BACKGROUND_COLOR = color(0, 0, 0, 0.0)
@SharedImmutable
private val DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE = ImageScaleType.FILL_X
@SharedImmutable
private val DEFAULT_BACKGROUND_IMAGE_GRAVITY = ImageGravity.CENTER

class LessonPage : BaseModel, Parent, Styles {
    internal companion object {
        @VisibleForTesting
        internal const val DEFAULT_TEXT_SCALE = 1.0
    }

    val id get() = fileName ?: "${manifest.code}-$position"
    val position: Int

    @VisibleForTesting
    internal val fileName: String?

    val isHidden: Boolean
    val listeners: Set<EventId>

    @AndroidColorInt
    val backgroundColor: Color

    @VisibleForTesting
    internal val _backgroundImage: String?
    val backgroundImage get() = getResource(_backgroundImage)
    val backgroundImageGravity: ImageGravity
    val backgroundImageScaleType: ImageScaleType

    @AndroidColorInt
    private val _controlColor: Color?
    @get:AndroidColorInt
    val controlColor get() = _controlColor ?: manifest.lessonControlColor

    private val _textScale: Double
    override val textScale get() = _textScale * stylesParent.textScale

    override val content: List<Content>

    internal constructor(
        manifest: Manifest,
        position: Int,
        fileName: String?,
        parser: XmlPullParser
    ) : super(manifest) {
        this.fileName = fileName
        this.position = position

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

        _textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE

        val content = mutableListOf<Content>()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_LESSON -> when (parser.name) {
                    XML_CONTENT -> content += parseContent(parser)
                }
            }
        }
        this.content = content
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        manifest: Manifest,
        position: Int = 0,
        fileName: String? = null,
        textScale: Double = DEFAULT_TEXT_SCALE
    ) : super(manifest) {
        this.fileName = fileName
        this.position = position

        isHidden = false
        listeners = emptySet()

        backgroundColor = DEFAULT_BACKGROUND_COLOR
        _backgroundImage = null
        backgroundImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY
        backgroundImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

        _controlColor = null

        _textScale = textScale

        content = emptyList()
    }
}

@get:AndroidColorInt
val LessonPage?.backgroundColor get() = this?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR
val LessonPage?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
val LessonPage?.backgroundImageGravity get() = this?.backgroundImageGravity ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY

@get:AndroidColorInt
val LessonPage?.controlColor get() = this?.controlColor ?: DEFAULT_LESSON_CONTROL_COLOR
