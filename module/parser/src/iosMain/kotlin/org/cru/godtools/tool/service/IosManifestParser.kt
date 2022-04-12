package org.cru.godtools.tool.service

import kotlinx.coroutines.runBlocking
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.xml.XmlPullParserFactory

class IosManifestParser(parserFactory: XmlPullParserFactory, config: ParserConfig) :
    ManifestParser(parserFactory, config) {
    fun parseManifestBlocking(fileName: String, config: ParserConfig = this.config) =
        runBlocking { parseManifest(fileName, config) }
}
