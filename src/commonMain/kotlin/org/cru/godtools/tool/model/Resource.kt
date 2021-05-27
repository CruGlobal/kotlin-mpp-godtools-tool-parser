package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

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

    val name: String?
    val localName: String?
}
