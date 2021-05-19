package org.cru.godtools.tool.internal

import org.cru.godtools.tool.xml.IosXmlPullParserFactory
import org.cru.godtools.tool.xml.XmlPullParserFactory
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile

actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : IosXmlPullParserFactory() {
        override fun openFile(fileName: String) = this@TEST_XML_PULL_PARSER_FACTORY::class.qualifiedName
            ?.replace('.', '/')?.substringBeforeLast('/')
            ?.let { NSBundle.mainBundle.pathForResource(fileName, null, it) }
            ?.let { NSData.dataWithContentsOfFile(it) }
    }

// region Android Robolectric
actual abstract class Runner
actual class AndroidJUnit4 : Runner()
// endregion Android Robolectric
