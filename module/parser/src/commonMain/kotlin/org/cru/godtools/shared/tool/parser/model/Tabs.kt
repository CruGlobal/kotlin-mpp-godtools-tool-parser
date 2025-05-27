package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_TAB = "tab"
private const val XML_LABEL = "label"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class Tabs : Content {
    internal companion object {
        internal const val XML_TABS = "tabs"
    }

    @JsExport.Ignore
    @JsName("_tabs")
    val tabs: List<Tab>
    override val tips get() = tabs.flatMap { it.contentTips }

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

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("tabs")
    val jsTabs get() = tabs.toTypedArray()
    // endregion Kotlin/JS interop

    class Tab : BaseModel, Parent, HasAnalyticsEvents {
        private val tabs: Tabs
        val position get() = tabs.tabs.indexOf(this)

        @JsName("_listeners")
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
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                    }

                    XMLNS_CONTENT -> when (parser.name) {
                        XML_LABEL -> label = parser.parseTextChild(this, XMLNS_CONTENT, XML_LABEL)
                    }
                }
            }
            this.label = label
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

        // region HasAnalyticsEvents
        @VisibleForTesting
        internal val analyticsEvents: List<AnalyticsEvent>

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.CLICKED -> analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.DEFAULT) }
            else -> error("The $type trigger type is currently unsupported on Tabs")
        }
        // endregion HasAnalyticsEvents

        // region Kotlin/JS interop
        @HiddenFromObjC
        @JsName("listeners")
        val jsListeners get() = listeners.toTypedArray()
        // endregion Kotlin/JS interop
    }
}
