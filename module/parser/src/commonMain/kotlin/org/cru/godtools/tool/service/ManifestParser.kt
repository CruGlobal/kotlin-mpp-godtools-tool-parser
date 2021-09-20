package org.cru.godtools.tool.service

import org.cru.godtools.tool.internal.FileNotFoundException
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.xml.XmlPullParserException
import org.cru.godtools.tool.xml.XmlPullParserFactory

open class ManifestParser(private val parserFactory: XmlPullParserFactory) {
    suspend fun parseManifest(fileName: String): ParserResult = try {
        val manifest = Manifest.parse(fileName) {
            parserFactory.getXmlParser(it)?.apply { nextTag() } ?: throw FileNotFoundException(fileName)
        }
        ParserResult.Data(manifest)
    } catch (e: FileNotFoundException) {
        ParserResult.Error.NotFound(e)
    } catch (e: XmlPullParserException) {
        ParserResult.Error.Corrupted(e)
    }
}

sealed class ParserResult {
    class Data(val manifest: Manifest) : ParserResult()

    open class Error internal constructor(val error: Exception? = null) : ParserResult() {
        class Corrupted internal constructor(e: Exception? = null) : Error(e)
        class NotFound internal constructor(e: Exception? = null) : Error(e)
    }
}
