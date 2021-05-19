package org.cru.godtools.tool.xml

import org.cru.godtools.tool.internal.DOMParser

private const val FILE_TYPE = "application/xml"

abstract class JsXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun readFile(fileName: String): String?

    override fun getXmlParser(fileName: String) =
        readFile(fileName)?.let { JsXmlPullParser(DOMParser().parseFromString(it, FILE_TYPE)) }
}
