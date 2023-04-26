package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_TAB = "tab"
private const val XML_LABEL = "label"

class Tabs : Content {
    internal companion object {
        private const val CONTENT_TYPE = "tabs"
        internal const val XML_TABS = "tabs"
    }

    val tabs: List<Tab>
    override val tips get() = tabs.flatMap { it.contentTips }

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, CONTENT_TYPE, parser) {
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

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(parent: Base = Manifest()) : super(parent, CONTENT_TYPE) {
        tabs = emptyList()
    }

    class Tab : BaseModel, Parent, HasAnalyticsEvents {
        private val tabs: Tabs
        val position get() = tabs.tabs.indexOf(this)

        val listeners: Set<EventId>
        val label: Text?

        override val content: List<Content>

        internal constructor(parent: Tabs, parser: XmlPullParser) : super(parent) {
            tabs = parent

            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TAB)
            listeners = parser.getAttributeValue(XML_LISTENERS)?.toEventIds()?.toSet().orEmpty()

            analyticsEvents = mutableListOf()
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
            this.label = label
        }

        @RestrictTo(RestrictToScope.TESTS)
        internal constructor(
            parent: Tabs = Tabs(),
            analyticsEvents: List<AnalyticsEvent> = emptyList()
        ) : super(parent) {
            tabs = parent
            this.analyticsEvents = analyticsEvents
            listeners = emptySet()
            label = null
            content = emptyList()
        }

        // region HasAnalyticsEvents
        @VisibleForTesting
        internal val analyticsEvents: List<AnalyticsEvent>

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.CLICKED -> analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.DEFAULT) }
            else -> error("The $type trigger type is currently unsupported on Tabs")
        }
        // endregion HasAnalyticsEvents
    }
}
