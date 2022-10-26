package org.cru.godtools.shared.tool.parser.xml

import platform.Foundation.NSData
import platform.Foundation.NSXMLParser

abstract class IosXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun openFile(fileName: String): NSData?

    override suspend fun getXmlParser(fileName: String) = openFile(fileName)?.let { IosXmlPullParser(NSXMLParser(it)) }
}
