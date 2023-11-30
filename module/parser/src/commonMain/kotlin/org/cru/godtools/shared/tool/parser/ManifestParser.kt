package org.cru.godtools.shared.tool.parser

import deezer.kustomexport.KustomExport
import io.github.aakira.napier.Napier
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.internal.FileNotFoundException
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory

@KustomExport
open class ManifestParser(private val parserFactory: XmlPullParserFactory, val defaultConfig: ParserConfig) {
    suspend fun parseManifest(fileName: String) = parseManifest(fileName, defaultConfig)
    suspend fun parseManifest(fileName: String, config: ParserConfig) = withContext(config.parserDispatcher) {
        try {
            val manifest =
                Manifest.parse(fileName, config) {
                    parserFactory.getXmlParser(it)?.apply { nextTag() } ?: throw FileNotFoundException(fileName)
                }
            ParserResult.Data(manifest)
        } catch (e: CancellationException) {
            throw e
        } catch (e: FileNotFoundException) {
            ParserResult.Error.NotFound(e)
        } catch (e: XmlPullParserException) {
            ParserResult.Error.Corrupted(e)
        } catch (e: Exception) {
            Napier.e("Unexpected Parsing Exception", e, "ManifestParser")
            ParserResult.Error(e)
        }
    }
}

@JsExport
@OptIn(ExperimentalJsExport::class)
sealed class ParserResult {
    class Data(val manifest: Manifest) : ParserResult()

    @JsName("ParserError")
    open class Error
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    constructor(val error: Throwable? = null) : ParserResult() {
        class Corrupted internal constructor(e: Exception? = null) : Error(e)
        class NotFound internal constructor(e: Exception? = null) : Error(e)
    }
}
