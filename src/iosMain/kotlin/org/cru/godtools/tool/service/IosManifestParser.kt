package org.cru.godtools.tool.service

import kotlinx.coroutines.runBlocking
import org.cru.godtools.tool.xml.XmlPullParserFactory

class IosManifestParser(parserFactory: XmlPullParserFactory) : ManifestParser(parserFactory) {
    fun parseManifestBlocking(fileName: String) = runBlocking { parseManifest(fileName) }
}
