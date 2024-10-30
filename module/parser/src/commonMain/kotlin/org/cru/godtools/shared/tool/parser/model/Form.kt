package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

@JsExport
@OptIn(ExperimentalJsExport::class)
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

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(parent: Base, content: (Form) -> List<Content>) : super(parent) {
        this.content = content(this)
    }
}
