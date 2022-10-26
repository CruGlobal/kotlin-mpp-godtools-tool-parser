package org.cru.godtools.shared.tool.parser.internal

import org.cru.godtools.shared.tool.parser.xml.IosXmlPullParserFactory
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile

internal actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : IosXmlPullParserFactory() {
        override fun openFile(fileName: String) = this@TEST_XML_PULL_PARSER_FACTORY::class.qualifiedName
            ?.replace('.', '/')?.substringBeforeLast('/')
            ?.let { NSBundle.mainBundle.pathForResource(fileName, null, it) }
            ?.let { NSData.dataWithContentsOfFile(it) }
    }
