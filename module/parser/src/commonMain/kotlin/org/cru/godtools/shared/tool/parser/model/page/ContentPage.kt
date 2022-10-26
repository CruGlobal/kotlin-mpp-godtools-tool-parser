package org.cru.godtools.shared.tool.parser.model.page

import org.cru.godtools.shared.tool.parser.internal.RestrictTo
import org.cru.godtools.shared.tool.parser.internal.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_CONTENT = "content"

class ContentPage : Page, Parent {
    internal companion object {
        internal const val TYPE_CONTENT = "content"
    }

    override val analyticsEvents: List<AnalyticsEvent>
    override val content: List<Content>

    internal constructor(
        manifest: Manifest,
        fileName: String?,
        parser: XmlPullParser
    ) : super(manifest, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGE)
        parser.requirePageType(TYPE_CONTENT)

        analyticsEvents = mutableListOf()
        content = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }
                XMLNS_PAGE -> when (parser.name) {
                    XML_CONTENT -> content += parseContent(parser)
                }
            }
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        manifest: Manifest,
        id: String? = null,
        parentPage: String? = null
    ) : super(manifest, id = id, parentPage = parentPage) {
        analyticsEvents = emptyList()
        content = emptyList()
    }

    override fun supports(type: Manifest.Type) = type == Manifest.Type.CYOA
}
