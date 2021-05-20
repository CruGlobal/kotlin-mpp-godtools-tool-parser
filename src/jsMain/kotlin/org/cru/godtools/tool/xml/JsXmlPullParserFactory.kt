package org.cru.godtools.tool.xml

abstract class JsXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun readFile(fileName: String): String?

    override fun getXmlParser(fileName: String) = readFile(fileName)?.let { JsXmlPullParser(it) }
}
