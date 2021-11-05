package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
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

    @OptIn(ExperimentalStdlibApi::class)
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
    internal constructor(parent: Base, sections: (Accordion) -> List<Section>) : super(parent) {
        this.sections = sections(this)
    }

    class Section : BaseModel, Parent {
        private val accordion: Accordion
        val id: String get() = "section-${accordion.sections.indexOf(this)}"

        val header: Text?
        override val content: List<Content>

        internal constructor(parent: Accordion, parser: XmlPullParser) : super(parent) {
            accordion = parent
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_SECTION)

            // process any child elements
            var header: Text? = null
            content = parseContent(parser) {
                when (parser.namespace) {
                    XMLNS_CONTENT -> when (parser.name) {
                        XML_SECTION_HEADER -> header = parser.parseTextChild(this, XMLNS_CONTENT, XML_SECTION_HEADER)
                    }
                }
            }
            this.header = header
        }

        @RestrictTo(RestrictToScope.TESTS)
        internal constructor(accordion: Accordion, content: (Section) -> List<Content>) : super(accordion) {
            this.accordion = accordion
            header = null
            this.content = content(this)
        }
    }
}
