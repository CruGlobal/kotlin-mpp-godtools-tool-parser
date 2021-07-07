package org.cru.godtools.tool.service

import org.cru.godtools.tool.internal.FileNotFoundException
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.xml.XmlPullParserException
import org.cru.godtools.tool.xml.XmlPullParserFactory

open class ManifestParser(private val parserFactory: XmlPullParserFactory) {
    suspend fun parseManifest(fileName: String): Result = try {
        val manifest = Manifest.parse(fileName) {
            parserFactory.getXmlParser(it)?.apply { nextTag() } ?: throw FileNotFoundException()
        }
        Result.Data(manifest)
    } catch (e: FileNotFoundException) {
        Result.Error.NotFound
    } catch (e: XmlPullParserException) {
        Result.Error.Corrupted
    }
}

sealed class Result {
    class Data(val manifest: Manifest) : Result()

    open class Error : Result() {
        object Corrupted : Error()
        object NotFound : Error()
    }
}
