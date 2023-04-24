package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@OptIn(ExperimentalJsExport::class)
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
    @JsName("createTestParagraph")
    constructor(
        parent: Base = Manifest(),
        content: (Paragraph) -> List<Content> = { emptyList() }
    ) : super(parent) {
        this.content = content(this)
    }
}
