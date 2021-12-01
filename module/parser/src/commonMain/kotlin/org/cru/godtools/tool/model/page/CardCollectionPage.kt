package org.cru.godtools.tool.model.page

import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.XMLNS_ANALYTICS
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

class CardCollectionPage : Page {
    internal companion object {
        const val TYPE_CARD_COLLECTION = "cardcollection"
    }

    override val analyticsEvents: List<AnalyticsEvent>

    internal constructor(
        manifest: Manifest,
        fileName: String?,
        parser: XmlPullParser
    ) : super(manifest, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGE)
        parser.requirePageType(TYPE_CARD_COLLECTION)

        analyticsEvents = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }
//                XMLNS_PAGE -> when (parser.name) {
//                    XML_CONTENT -> content += parseContent(parser)
//                }
            }
        }
    }

    override fun supports(type: Manifest.Type) = type == Manifest.Type.CYOA
}
