package org.cru.godtools.shared.tool.parser

import kotlinx.coroutines.runBlocking
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory

class IosManifestParser(parserFactory: XmlPullParserFactory, defaultConfig: ParserConfig) :
    ManifestParser(parserFactory, defaultConfig) {
    fun parseManifestBlocking(fileName: String) = parseManifestBlocking(fileName, defaultConfig)
    fun parseManifestBlocking(fileName: String, config: ParserConfig) =
        runBlocking { parseManifest(fileName, config) }
}
