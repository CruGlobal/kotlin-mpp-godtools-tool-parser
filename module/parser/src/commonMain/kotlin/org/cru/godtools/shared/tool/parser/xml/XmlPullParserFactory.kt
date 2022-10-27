package org.cru.godtools.shared.tool.parser.xml

abstract class XmlPullParserFactory {
    internal abstract suspend fun getXmlParser(fileName: String): XmlPullParser?
}
