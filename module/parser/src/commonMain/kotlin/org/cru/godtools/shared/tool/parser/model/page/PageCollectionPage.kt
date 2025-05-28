package org.cru.godtools.shared.tool.parser.model.page

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.reflect.KClass
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.XMLNS_ANALYTICS
import org.cru.godtools.shared.tool.parser.model.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.util.setOnce
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

@JsExport
@OptIn(ExperimentalJsExport::class)
class PageCollectionPage : Page, HasPages {
    companion object {
        internal const val TYPE_PAGE_COLLECTION = "page-collection"

        private const val XML_PAGES = "pages"
        private const val XML_IMPORT = "import"
        private const val XML_IMPORT_FILENAME = "filename"

        const val PARENT_PARAM_ACTIVE_PAGE = "active-page"

        internal suspend fun parse(
            container: HasPages,
            fileName: String?,
            parser: XmlPullParser,
            parseFile: suspend (String) -> XmlPullParser
        ) = PageCollectionPage(container, fileName, parser).apply {
            buildPagesFromParsedPages(parseFile)
        }
    }

    override val analyticsEvents: List<AnalyticsEvent>
    private val parsedPages: List<PageOrImport>
    override var pages: List<Page> by setOnce()
        private set

    private constructor(
        container: HasPages,
        fileName: String?,
        parser: XmlPullParser
    ) : super(container, fileName, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_PAGE, XML_PAGE)
        parser.requirePageType(TYPE_PAGE_COLLECTION)

        analyticsEvents = mutableListOf()
        parsedPages = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                }

                XMLNS_PAGE -> when (parser.name) {
                    XML_PAGES -> parsedPages += parser.parsePages()
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
                            ?.let { add(PageOrImport(it)) }
                    }
                    XML_IMPORT -> add(PageOrImport(ref = getAttributeValue(XML_IMPORT_FILENAME)))
                }
            }
        }
    }

    private suspend fun buildPagesFromParsedPages(parseFile: suspend (String) -> XmlPullParser) {
        val pageIndex by lazy { manifest.pageXmlFiles.associate { it.name to it.src } }

        pages = coroutineScope {
            parsedPages
                .map {
                    it.page?.let { CompletableDeferred(it) }
                        ?: async {
                            val pageSrc = it.ref?.let { pageIndex[it] } ?: return@async null
                            Page.parse(this@PageCollectionPage, it.ref, parseFile(pageSrc), parseFile)
                        }
                }
                .awaitAll()
                .filterNotNull()
        }
    }

    override fun <T : Page> supportsPageType(type: KClass<T>) = type == ContentPage::class

    private class PageOrImport(val page: Page? = null, val ref: String? = null)
}
