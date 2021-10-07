package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.internal.DeprecationException
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val TAG = "Tabs"

private const val XML_TAB = "tab"
private const val XML_LABEL = "label"

class Tabs : Content {
    internal companion object {
        internal const val XML_TABS = "tabs"
    }

    val tabs: List<Tab>
    override val tips get() = tabs.flatMap { it.contentTips }

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

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(parent: Base = Manifest()) : super(parent) {
        tabs = emptyList()
    }

    class Tab : BaseModel, Parent, HasAnalyticsEvents {
        private val tabs: Tabs
        val position get() = tabs.tabs.indexOf(this)

        val analyticsEvents: List<AnalyticsEvent>
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

            // Log a non-fatal warning if any analytics event is still using the SELECTED trigger
            analyticsEvents.forEach {
                if (it.trigger == Trigger.SELECTED) {
                    val message =
                        "tool: ${manifest.code} locale: ${manifest.locale} action: ${it.action} trigger: ${it.trigger}"
                    Napier.e(message, DeprecationException("XML Analytics Deprecated trigger $message"), TAG)
                }
            }
        }

        @RestrictTo(RestrictTo.Scope.TESTS)
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

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.CLICKED ->
                analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.SELECTED, Trigger.DEFAULT) }
            else -> error("The $type trigger type is currently unsupported on Tabs")
        }
    }
}
