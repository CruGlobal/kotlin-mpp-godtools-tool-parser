package org.cru.godtools.shared.tool.parser.model.lesson

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import com.github.ajalt.colormath.Color
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.XML_EVENTS
import org.cru.godtools.shared.tool.parser.model.page.Page
import org.cru.godtools.shared.tool.parser.model.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_CONTENT = "content"

@JsExport
@OptIn(ExperimentalJsExport::class)
class LessonPage : Page, Parent {
    @VisibleForTesting
    override val analyticsEvents: List<AnalyticsEvent>

    override val content: List<Content>

    internal constructor(
        container: HasPages,
        fileName: String?,
        parser: XmlPullParser
    ) : super(container, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_LESSON, XML_PAGE)

        analyticsEvents = mutableListOf()
        content = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                }

                XMLNS_LESSON -> when (parser.name) {
                    XML_CONTENT -> content += parseContent(parser)
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        manifest: Manifest = Manifest(),
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        backgroundColor: Color = DEFAULT_BACKGROUND_COLOR,
        backgroundImage: String? = null,
        backgroundImageGravity: Gravity = DEFAULT_BACKGROUND_IMAGE_GRAVITY,
        backgroundImageScaleType: ImageScaleType = DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE,
        controlColor: Color? = null,
        textScale: Double = DEFAULT_TEXT_SCALE
    ) : super(
        manifest,
        backgroundColor = backgroundColor,
        backgroundImage = backgroundImage,
        backgroundImageGravity = backgroundImageGravity,
        backgroundImageScaleType = backgroundImageScaleType,
        controlColor = controlColor,
        textScale = textScale
    ) {
        this.analyticsEvents = analyticsEvents

        content = emptyList()
    }
}
