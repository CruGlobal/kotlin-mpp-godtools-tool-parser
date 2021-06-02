package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser

class Paragraph : Content, Parent {
    companion object {
        internal const val XML_PARAGRAPH = "paragraph"
        internal const val XML_FALLBACK = "fallback"
    }

    override val content: List<Content>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_PARAGRAPH)
        content = parseContent(parser)
    }
}
