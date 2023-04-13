package org.cru.godtools.shared.tool.parser.xml

import kotlinx.coroutines.await
import kotlin.js.Promise

@JsExport
@OptIn(ExperimentalJsExport::class)
abstract class JsXmlPullParserFactory : XmlPullParserFactory() {
    abstract fun readFile(fileName: String): Promise<String?>

    override suspend fun getXmlParser(fileName: String) = readFile(fileName).await()?.let { JsXmlPullParser(it) }
}
