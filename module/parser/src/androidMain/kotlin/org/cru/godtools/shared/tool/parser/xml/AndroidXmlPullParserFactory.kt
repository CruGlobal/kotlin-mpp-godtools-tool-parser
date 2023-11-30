package org.cru.godtools.shared.tool.parser.xml

import android.util.Xml
import java.io.InputStream
import org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES

abstract class AndroidXmlPullParserFactory : XmlPullParserFactory() {
    protected abstract suspend fun openFile(fileName: String): InputStream?

    override suspend fun getXmlParser(fileName: String) = openFile(fileName)
        ?.let { AndroidXmlPullParser(it.xmlPullParser()) }
}

private fun InputStream.xmlPullParser() = Xml.newPullParser().also {
    it.setFeature(FEATURE_PROCESS_NAMESPACES, true)
    it.setInput(this, "UTF-8")
}
