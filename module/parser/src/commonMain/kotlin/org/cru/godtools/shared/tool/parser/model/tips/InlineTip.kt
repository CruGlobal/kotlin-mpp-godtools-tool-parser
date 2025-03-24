package org.cru.godtools.shared.tool.parser.model.tips

import androidx.annotation.RestrictTo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.tool.parser.model.Base
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_ID = "id"

@JsExport
@OptIn(ExperimentalJsExport::class)
class InlineTip : Content {
    internal companion object {
        internal const val XML_TIP = "tip"
    }

    val id: String?
    val tip get() = manifest.findTip(id)

    override val tips get() = listOfNotNull(tip)

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRAINING, XML_TIP)

        id = parser.getAttributeValue(XML_ID)

        parser.skipTag()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    @JsName("createTestInlineTip")
    constructor(parent: Base, id: String? = null) : super(parent) {
        this.id = id
    }
}
