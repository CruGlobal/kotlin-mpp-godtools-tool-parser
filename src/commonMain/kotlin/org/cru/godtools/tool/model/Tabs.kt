package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_TAB = "tab"
private const val XML_LABEL = "label"

class Tabs : Content {
    internal companion object {
        internal const val XML_TABS = "tabs"
    }

    val tabs: List<Tab>
    override val tips get() = tabs.flatMap { it.contentTips }

    @VisibleForTesting
    internal constructor(parent: Base, tabs: List<Tab>) : super(parent) {
        this.tabs = tabs
    }

    @OptIn(ExperimentalStdlibApi::class)
    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TABS)

        tabs = buildList {
            parser.parseChildren {
                when (parser.namespace) {
                    XMLNS_CONTENT -> when (parser.name) {
                        XML_TAB -> add(Tab(this@Tabs, parser))
                    }
                }
            }
        }
    }

    class Tab : BaseModel, Parent {
        private val tabs: Tabs
        val position get() = tabs.tabs.indexOf(this)

        val analyticsEvents: Collection<AnalyticsEvent>
        val listeners: Set<EventId>
        val label: Text?

        override val content: List<Content>

        internal constructor(parent: Tabs, parser: XmlPullParser) : super(parent) {
            tabs = parent

            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TAB)
            listeners = parser.getAttributeValue(XML_LISTENERS)?.toEventIds()?.toSet().orEmpty()

            var analyticsEvents: Collection<AnalyticsEvent> = emptyList()
            var label: Text? = null
            content = parseContent(parser) {
                when (parser.namespace) {
                    XMLNS_ANALYTICS -> when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                    }
                    XMLNS_CONTENT -> when (parser.name) {
                        XML_LABEL -> label = parser.parseTextChild(this, XMLNS_CONTENT, XML_LABEL)
                    }
                }
            }
            this.analyticsEvents = analyticsEvents
            this.label = label
        }
    }
}
