package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_MANIFEST = "manifest"

class Manifest {
    internal constructor(parser: XmlPullParser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_MANIFEST)
    }
}
