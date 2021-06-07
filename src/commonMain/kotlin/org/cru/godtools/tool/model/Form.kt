package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser

class Form internal constructor(parent: Base, parser: XmlPullParser) : Content(parent, parser), Parent {
    companion object {
        internal const val XML_FORM = "form"
    }

    override val content: List<Content>

    init {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FORM)
        content = parseContent(parser)
    }
}
