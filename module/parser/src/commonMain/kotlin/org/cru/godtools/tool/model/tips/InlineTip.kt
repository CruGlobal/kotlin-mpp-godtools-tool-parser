package org.cru.godtools.tool.model.tips

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.Base
import org.cru.godtools.tool.model.Content
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_ID = "id"

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
    constructor(parent: Base, id: String? = null) : super(parent) {
        this.id = id
    }
}
