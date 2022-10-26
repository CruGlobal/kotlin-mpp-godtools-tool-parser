package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.internal.RestrictTo
import org.cru.godtools.shared.tool.parser.internal.RestrictToScope
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_FILENAME = "filename"
private const val XML_SRC = "src"

class Resource : BaseModel {
    internal companion object {
        internal const val XML_RESOURCE = "resource"
    }

    internal constructor(manifest: Manifest, parser: XmlPullParser) : super(manifest) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_RESOURCE)

        name = parser.getAttributeValue(null, XML_FILENAME)
        localName = parser.getAttributeValue(null, XML_SRC)

        parser.skipTag()
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(manifest: Manifest = Manifest(), name: String? = null) : super(manifest) {
        this.name = name
        localName = null
    }

    val name: String?
    val localName: String?
}
