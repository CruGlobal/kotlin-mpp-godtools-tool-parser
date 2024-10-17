package org.cru.godtools.shared.tool.parser.model.page

import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_PAGE_COLLECTION
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

class PageCollectionPage : Page, HasPages {
    companion object {
        internal const val TYPE_PAGE_COLLECTION = "page-collection"

        private const val XML_PAGES = "pages"
    }

    override val analyticsEvents: List<AnalyticsEvent>
    override val pages: List<Page>

    internal constructor(
        container: HasPages,
        fileName: String?,
        parser: XmlPullParser
    ) : super(container, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGE)
        parser.requirePageType(TYPE_PAGE_COLLECTION)

        analyticsEvents = mutableListOf()
        pages = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }

                XMLNS_PAGE -> when (parser.name) {
                    XML_PAGES -> pages += parser.parsePages()
                }
            }
        }
    }

    private fun XmlPullParser.parsePages() = buildList {
        require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGES)

        // process any child elements
        parseChildren {
            when (namespace) {
                XMLNS_PAGE -> when (name) {
                    XML_PAGE -> {
                        parse(this@PageCollectionPage, null, this@parsePages)
                            ?.takeIf { it is ContentPage }
                            ?.let { add(it) }
                    }
                }
            }
        }
    }

    override fun supports(type: Manifest.Type) =
        type == Manifest.Type.CYOA && manifest.config.supportsFeature(FEATURE_PAGE_COLLECTION)
}
