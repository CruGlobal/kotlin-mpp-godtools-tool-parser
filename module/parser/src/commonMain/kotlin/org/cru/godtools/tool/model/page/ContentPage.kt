package org.cru.godtools.tool.model.page

import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Parent
import org.cru.godtools.tool.model.XMLNS_ANALYTICS
import org.cru.godtools.tool.model.parseContent
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

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

    override fun supports(type: Manifest.Type) = type == Manifest.Type.CYOA
}
