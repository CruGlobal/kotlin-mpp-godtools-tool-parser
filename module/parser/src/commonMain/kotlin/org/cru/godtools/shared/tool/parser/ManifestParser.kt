package org.cru.godtools.shared.tool.parser

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import org.cru.godtools.shared.tool.parser.internal.FileNotFoundException
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory

open class ManifestParser(private val parserFactory: XmlPullParserFactory, val defaultConfig: ParserConfig) {
    suspend fun parseManifest(fileName: String, config: ParserConfig = defaultConfig): ParserResult = try {
        val manifest = Manifest.parse(fileName, config) {
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

sealed class ParserResult {
    class Data(val manifest: Manifest) : ParserResult()

    open class Error internal constructor(val error: Exception? = null) : ParserResult() {
        class Corrupted internal constructor(e: Exception? = null) : Error(e)
        class NotFound internal constructor(e: Exception? = null) : Error(e)
    }
}
