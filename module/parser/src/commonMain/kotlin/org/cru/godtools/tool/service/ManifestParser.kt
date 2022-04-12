package org.cru.godtools.tool.service

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.FileNotFoundException
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.xml.XmlPullParserException
import org.cru.godtools.tool.xml.XmlPullParserFactory

open class ManifestParser(private val parserFactory: XmlPullParserFactory, protected val config: ParserConfig) {
    suspend fun parseManifest(fileName: String, config: ParserConfig = this.config): ParserResult = try {
        val manifest = Manifest.parse(fileName, config) {
            parserFactory.getXmlParser(it)?.apply { nextTag() } ?: throw FileNotFoundException(fileName)
        }
        ParserResult.Data(manifest)
    } catch (e: FileNotFoundException) {
        ParserResult.Error.NotFound(e)
    } catch (e: XmlPullParserException) {
        ParserResult.Error.Corrupted(e)
    } catch (e: Exception) {
        Napier.d("Unexpected Parsing Exception", e, "ManifestParser")
        ParserResult.Error(e)
    }
}

sealed class ParserResult {
    class Data(val manifest: Manifest) : ParserResult()

    open class Error internal constructor(val error: Exception? = null) : ParserResult() {
        class Corrupted internal constructor(e: Exception? = null) : Error(e)
        class NotFound internal constructor(e: Exception? = null) : Error(e)
    }
}
