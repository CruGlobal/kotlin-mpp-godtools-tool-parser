package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.internal.RestrictTo
import org.cru.godtools.shared.tool.parser.internal.RestrictToScope
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

class Paragraph : Content, Parent {
    internal companion object {
        internal const val XML_PARAGRAPH = "paragraph"
        internal const val XML_FALLBACK = "fallback"
    }

    override val content: List<Content>
    override val tips get() = contentTips

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_PARAGRAPH)
        content = parseContent(parser)
    }

    @RestrictTo(RestrictToScope.TESTS)
    constructor(parent: Base, content: (Paragraph) -> List<Content>) : super(parent) {
        this.content = content(this)
    }
}
