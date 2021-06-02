package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.XmlPullParserException

class Fallback : Content, Parent {
    companion object {
        internal const val XML_FALLBACK = "fallback"
    }

    override val content: List<Content>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        when (parser.name) {
            Paragraph.XML_PARAGRAPH -> {
                parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, Paragraph.XML_PARAGRAPH)
                if (parser.getAttributeValue(Paragraph.XML_FALLBACK)?.toBoolean() != true) {
                    throw XmlPullParserException("expected fallback=\"true\"")
                }
            }
            else -> parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FALLBACK)
        }

        content = parseContent(parser)
    }
}
