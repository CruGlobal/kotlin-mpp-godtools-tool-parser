package org.cru.godtools.tool.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES
import java.io.InputStream

abstract class AndroidXmlPullParserFactory : XmlPullParserFactory() {
    protected abstract suspend fun openFile(fileName: String): InputStream?

    override suspend fun getXmlParser(fileName: String) = openFile(fileName)
        ?.let { AndroidXmlPullParser(it.xmlPullParser()) }
}

private fun InputStream.xmlPullParser() = Xml.newPullParser().also {
    it.setFeature(FEATURE_PROCESS_NAMESPACES, true)
    it.setInput(this, "UTF-8")
}
