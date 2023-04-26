package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
@OptIn(ExperimentalJsExport::class)
class Form : Content, Parent {
    internal companion object {
        private const val CONTENT_TYPE = "form"
        internal const val XML_FORM = "form"
    }

    override val content: List<Content>
    override val tips get() = contentTips

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, CONTENT_TYPE, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FORM)
        content = parseContent(parser)
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(parent: Base, content: (Form) -> List<Content>) : super(parent, CONTENT_TYPE) {
        this.content = content(this)
    }
}
