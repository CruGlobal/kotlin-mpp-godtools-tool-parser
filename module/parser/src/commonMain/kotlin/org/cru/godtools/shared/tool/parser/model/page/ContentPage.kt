package org.cru.godtools.shared.tool.parser.model.page

import androidx.annotation.RestrictTo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.XML_EVENTS
import org.cru.godtools.shared.tool.parser.model.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_CONTENT = "content"

@JsExport
@OptIn(ExperimentalJsExport::class)
class ContentPage : Page, Parent {
    internal companion object {
        internal const val TYPE_CONTENT = "content"
    }

    override val analyticsEvents: List<AnalyticsEvent>
    override val content: List<Content>

    internal constructor(
        container: HasPages,
        fileName: String?,
        parser: XmlPullParser
    ) : super(container, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGE)
        parser.requirePageType(TYPE_CONTENT)

        analyticsEvents = mutableListOf()
        content = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                }

                XMLNS_PAGE -> when (parser.name) {
                    XML_CONTENT -> content += parseContent(parser)
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        container: HasPages,
        id: String? = null,
        parentPage: String? = null
    ) : super(container, id = id, parentPage = parentPage) {
        analyticsEvents = emptyList()
        content = emptyList()
    }
}
