package org.cru.godtools.shared.tool.parser.xml

import kotlin.js.Promise
import kotlinx.coroutines.await

@JsExport
@OptIn(ExperimentalJsExport::class)
abstract class JsXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun readFile(fileName: String): Promise<String?>

    override suspend fun getXmlParser(fileName: String) = readFile(fileName).await()?.let { JsXmlPullParser(it) }
}
