package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

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
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) continue

                when (parser.namespace) {
                    XMLNS_CONTENT -> when (parser.name) {
                        Tab.XML_TAB -> add(Tab(this@Tabs, size, parser))
                        else -> parser.skipTag()
                    }
                    else -> parser.skipTag()
                }
            }
        }
    }

    class Tab internal constructor(parent: Tabs, val position: Int, parser: XmlPullParser) : BaseModel(parent), Parent {
        internal companion object {
            internal const val XML_TAB = "tab"

            private const val XML_LABEL = "label"
        }

        val analyticsEvents: Collection<AnalyticsEvent>
        val listeners: Set<EventId>
        val label: Text?

        override val content: List<Content>

        init {
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TAB)
            listeners = parser.getAttributeValue(XML_LISTENERS)?.toEventIds()?.toSet().orEmpty()

            // process any child elements
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
