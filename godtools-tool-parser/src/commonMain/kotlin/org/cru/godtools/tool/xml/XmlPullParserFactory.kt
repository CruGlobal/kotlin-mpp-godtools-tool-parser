package org.cru.godtools.tool.xml

abstract class XmlPullParserFactory {
    internal abstract suspend fun getXmlParser(fileName: String): XmlPullParser?
}
