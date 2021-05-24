package org.cru.godtools.tool.xml

import platform.Foundation.NSData
import platform.Foundation.NSXMLParser

abstract class IosXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun openFile(fileName: String): NSData?

    override fun getXmlParser(fileName: String) =
        openFile(fileName)?.let { IosXmlPullParser(NSXMLParser(it)) }
}
