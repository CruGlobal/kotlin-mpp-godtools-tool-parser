package org.cru.godtools.shared.tool.parser

import co.touchlab.kermit.Logger
import deezer.kustomexport.KustomExport
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
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
            Logger.e("Unexpected Parsing Exception", e, "ManifestParser")
            ParserResult.Error(e)
        }
    }
}

@JsExport
@OptIn(ExperimentalJsExport::class)
sealed class ParserResult {
    class Data(val manifest: Manifest) : ParserResult()

    @JsName("ParserError")
    open class Error protected constructor(val error: Throwable? = null) : ParserResult() {
        internal constructor(e: Exception?) : this(e as? Throwable)

        class Corrupted internal constructor(e: Exception? = null) : Error(e)
        class NotFound internal constructor(e: Exception? = null) : Error(e)
    }
}
