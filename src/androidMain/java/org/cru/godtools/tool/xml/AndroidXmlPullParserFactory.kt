package org.cru.godtools.tool.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES
import java.io.InputStream

abstract class AndroidXmlPullParserFactory : XmlPullParserFactory() {
    protected abstract fun openFile(fileName: String): InputStream

    override fun getXmlParser(fileName: String) = openFile(fileName)
        .xmlPullParser()
        .let { AndroidXmlPullParser(it) }
}

private fun InputStream.xmlPullParser() = Xml.newPullParser().also {
    it.setFeature(FEATURE_PROCESS_NAMESPACES, true)
    it.setInput(this, "UTF-8")
}
