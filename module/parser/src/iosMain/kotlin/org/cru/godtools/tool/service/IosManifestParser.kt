package org.cru.godtools.tool.service

import kotlinx.coroutines.runBlocking
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.xml.XmlPullParserFactory

class IosManifestParser(parserFactory: XmlPullParserFactory, defaultConfig: ParserConfig) :
    ManifestParser(parserFactory, defaultConfig) {
    fun parseManifestBlocking(fileName: String) = parseManifestBlocking(fileName, defaultConfig)
    fun parseManifestBlocking(fileName: String, config: ParserConfig) =
        runBlocking { parseManifest(fileName, config) }
}
