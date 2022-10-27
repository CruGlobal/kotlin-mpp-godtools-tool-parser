package org.cru.godtools.shared.tool.parser.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cru.godtools.shared.tool.parser.xml.AndroidXmlPullParserFactory
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory

internal actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : AndroidXmlPullParserFactory() {
        override suspend fun openFile(fileName: String) = withContext(Dispatchers.IO) {
            this@TEST_XML_PULL_PARSER_FACTORY::class.java.getResourceAsStream(fileName)
        }
    }
