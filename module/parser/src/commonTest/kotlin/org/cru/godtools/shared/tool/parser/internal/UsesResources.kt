package org.cru.godtools.shared.tool.parser.internal

import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory

abstract class UsesResources(internal val resourcesDir: String? = "model") {
    internal suspend fun getTestXmlParser(name: String) =
        TEST_XML_PULL_PARSER_FACTORY.getXmlParser(name)!!.apply { nextTag() }
}
internal expect val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
