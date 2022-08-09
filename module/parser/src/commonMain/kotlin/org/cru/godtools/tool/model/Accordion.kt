package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_SECTION = "section"
private const val XML_SECTION_HEADER = "header"

class Accordion : Content {
    internal companion object {
        internal const val XML_ACCORDION = "accordion"
    }

    val sections: List<Section>
    override val tips get() = sections.flatMap { it.contentTips }

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_ACCORDION)

        sections = buildList {
            parser.parseChildren {
                when (parser.namespace) {
                    XMLNS_CONTENT -> when (parser.name) {
                        XML_SECTION -> add(Section(this@Accordion, parser))
                    }
                }
            }
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        sections: ((Accordion) -> List<Section>)? = null
    ) : super(parent) {
        this.sections = sections?.invoke(this).orEmpty()
    }

    class Section : BaseModel, Parent, HasAnalyticsEvents {
        private val accordion: Accordion
        val id: String get() = "section-${accordion.sections.indexOf(this)}"

        val header: Text?
        private val analyticsEvents: List<AnalyticsEvent>
        override val content: List<Content>

        internal constructor(parent: Accordion, parser: XmlPullParser) : super(parent) {
            accordion = parent
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_SECTION)

            // process any child elements
            var header: Text? = null
            analyticsEvents = mutableListOf()
            content = parseContent(parser) {
                when (parser.namespace) {
                    XMLNS_ANALYTICS -> when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                    }
                    XMLNS_CONTENT -> when (parser.name) {
                        XML_SECTION_HEADER -> header = parser.parseTextChild(this, XMLNS_CONTENT, XML_SECTION_HEADER)
                    }
                }
            }
            this.header = header
        }

        @RestrictTo(RestrictToScope.TESTS)
        constructor(
            accordion: Accordion = Accordion(),
            analyticsEvents: List<AnalyticsEvent> = emptyList(),
            content: ((Section) -> List<Content>)? = null
        ) : super(accordion) {
            this.accordion = accordion
            header = null
            this.analyticsEvents = analyticsEvents
            this.content = content?.invoke(this).orEmpty()
        }

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
            Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
            else -> error("The $type trigger type is currently unsupported on Tabs")
        }
    }
}
