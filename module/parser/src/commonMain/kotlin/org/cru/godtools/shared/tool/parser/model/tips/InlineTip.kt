package org.cru.godtools.shared.tool.parser.model.tips

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.Base
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

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

        id = parser.getAttributeValue(null, XML_ID)

        parser.skipTag()
    }

    @RestrictTo(RestrictToScope.TESTS)
    @JsName("createTestInlineTip")
    constructor(parent: Base, id: String? = null) : super(parent) {
        this.id = id
    }
}
