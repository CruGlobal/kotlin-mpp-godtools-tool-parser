package org.cru.godtools.shared.tool.parser.xml

abstract class JsXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun readFile(fileName: String): String?

    override suspend fun getXmlParser(fileName: String) = readFile(fileName)?.let { JsXmlPullParser(it) }
}
