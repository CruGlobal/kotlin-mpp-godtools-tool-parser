package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC

private const val XML_SECTION = "section"
private const val XML_SECTION_HEADER = "header"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class Accordion : Content {
    internal companion object {
        internal const val XML_ACCORDION = "accordion"
    }

    @JsName("_sections")
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

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("sections")
    val jsSections get() = sections.toTypedArray()
    // endregion Kotlin/JS interop

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
            analyticsEvents: ((Section) -> List<AnalyticsEvent>)? = null,
            content: ((Section) -> List<Content>)? = null
        ) : super(accordion) {
            this.accordion = accordion
            header = null
            this.analyticsEvents = analyticsEvents?.invoke(this).orEmpty()
            this.content = content?.invoke(this).orEmpty()
        }

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.VISIBLE -> analyticsEvents.filter { it.isTriggerType(Trigger.VISIBLE, Trigger.DEFAULT) }
            Trigger.HIDDEN -> analyticsEvents.filter { it.isTriggerType(Trigger.HIDDEN) }
            else -> error("The $type trigger type is currently unsupported on Tabs")
        }
    }
}
