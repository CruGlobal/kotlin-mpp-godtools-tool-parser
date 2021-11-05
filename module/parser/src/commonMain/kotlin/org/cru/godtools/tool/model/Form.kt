package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.xml.XmlPullParser

class Form : Content, Parent {
    internal companion object {
        internal const val XML_FORM = "form"
    }

    override val content: List<Content>
    override val tips get() = contentTips

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FORM)
        content = parseContent(parser)
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(parent: Base, content: (Form) -> List<Content>) : super(parent) {
        this.content = content(this)
    }
}
