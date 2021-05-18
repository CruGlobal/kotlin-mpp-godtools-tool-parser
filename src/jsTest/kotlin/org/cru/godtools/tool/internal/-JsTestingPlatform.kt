package org.cru.godtools.tool.internal

import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.XmlPullParserFactory

actual val Any.TEST_XML_PULL_PARSER_FACTORY get() = object : XmlPullParserFactory() {
    override fun getXmlParser(fileName: String): XmlPullParser? = null
}

// region Android Robolectric
actual abstract class Runner
actual class AndroidJUnit4 : Runner()
// endregion Android Robolectric
