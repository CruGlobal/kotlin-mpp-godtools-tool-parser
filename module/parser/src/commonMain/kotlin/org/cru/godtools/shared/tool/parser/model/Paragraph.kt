package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

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

    @RestrictTo(RestrictTo.Scope.TESTS)
    @JsName("createTestParagraph")
    constructor(
        parent: Base = Manifest(),
        content: (Paragraph) -> List<Content> = { emptyList() }
    ) : super(parent) {
        this.content = content(this)
    }
}
