package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
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

    @JsName("createTestForm")
    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(parent: Base = Manifest(), content: ((Form) -> List<Content>)? = null) : super(parent) {
        this.content = content?.invoke(this).orEmpty()
    }
}

@HiddenFromObjC
@get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@OptIn(ExperimentalObjCRefinement::class)
val Base.formParent: Form? get() = parent as? Form ?: parent?.formParent
