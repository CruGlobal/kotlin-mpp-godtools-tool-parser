package org.cru.godtools.tool.service

import org.cru.godtools.tool.internal.FileNotFoundException
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.xml.XmlPullParserException
import org.cru.godtools.tool.xml.XmlPullParserFactory

class ManifestParser(private val parserFactory: XmlPullParserFactory) {
    suspend fun parseManifest(fileName: String): Result = try {
        val manifest = Manifest.parse(fileName) {
            parserFactory.getXmlParser(it)?.apply { nextTag() } ?: throw FileNotFoundException(fileName)
        }
        Result.Data(manifest)
    } catch (e: FileNotFoundException) {
        Result.Error.NotFound(e)
    } catch (e: XmlPullParserException) {
        Result.Error.Corrupted(e)
    }
}

sealed class Result {
    class Data(val manifest: Manifest) : Result()

    open class Error internal constructor(val error: Exception? = null) : Result() {
        class Corrupted internal constructor(e: Exception? = null) : Error(e)
        class NotFound internal constructor(e: Exception? = null) : Error(e)
    }
}
