package org.cru.godtools.shared.tool.parser.xml

@JsExport
@OptIn(ExperimentalJsExport::class)
abstract class JsXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun readFile(fileName: String): String?

    override suspend fun getXmlParser(fileName: String) = readFile(fileName)?.let { JsXmlPullParser(it) }
}
