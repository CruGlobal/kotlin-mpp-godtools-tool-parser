package org.cru.godtools.shared.tool.parser.xml

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
@OptIn(ExperimentalJsExport::class)
abstract class XmlPullParserFactory {
    internal abstract suspend fun getXmlParser(fileName: String): XmlPullParser?
}
