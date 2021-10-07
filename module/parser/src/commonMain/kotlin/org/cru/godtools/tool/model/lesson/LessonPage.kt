package org.cru.godtools.tool.model.lesson

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.PlatformColor
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.XMLNS_ANALYTICS
import org.cru.godtools.tool.model.page.DEFAULT_CONTROL_COLOR
import org.cru.godtools.tool.model.page.Page
import org.cru.godtools.tool.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.tool.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.page.XML_CONTROL_COLOR
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_PAGE = "page"
private const val XML_HIDDEN = "hidden"
private const val XML_CONTENT = "content"

class LessonPage : Page, Parent {
    val isHidden: Boolean

    @VisibleForTesting
    override val analyticsEvents: List<AnalyticsEvent>

    @AndroidColorInt
    private val _controlColor: PlatformColor?
    @get:AndroidColorInt
    internal val controlColor get() = _controlColor ?: manifest.pageControlColor

    override val content: List<Content>

    internal constructor(
        manifest: Manifest,
        fileName: String?,
        parser: XmlPullParser
    ) : super(manifest, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_LESSON, XML_PAGE)

        isHidden = parser.getAttributeValue(XML_HIDDEN)?.toBoolean() ?: false

        _controlColor = parser.getAttributeValue(XML_CONTROL_COLOR)?.toColorOrNull()

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
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        backgroundColor: PlatformColor = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        backgroundImageGravity: ImageGravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        controlColor: PlatformColor? = null,
        textScale: Double = DEFAULT_TEXT_SCALE
    ) : super(
        manifest,
        backgroundColor = backgroundColor,
        backgroundImage = backgroundImage,
        backgroundImageGravity = backgroundImageGravity,
        backgroundImageScaleType = backgroundImageScaleType,
        textScale = textScale
    ) {
        isHidden = false

        this.analyticsEvents = analyticsEvents

        _controlColor = controlColor

        content = emptyList()
    }
}

@get:AndroidColorInt
val LessonPage?.backgroundColor get() = this?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR
val LessonPage?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
val LessonPage?.backgroundImageGravity get() = this?.backgroundImageGravity ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY

@get:AndroidColorInt
val LessonPage?.controlColor get() = this?.controlColor ?: DEFAULT_CONTROL_COLOR
