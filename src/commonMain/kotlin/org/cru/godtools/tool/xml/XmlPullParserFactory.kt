package org.cru.godtools.tool.xml

abstract class XmlPullParserFactory {
    internal abstract fun getXmlParser(fileName: String): XmlPullParser?
}
