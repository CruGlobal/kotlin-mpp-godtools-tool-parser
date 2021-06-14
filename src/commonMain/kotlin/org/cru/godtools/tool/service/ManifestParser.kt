package org.cru.godtools.tool.service

import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.xml.XmlPullParserException
import org.cru.godtools.tool.xml.XmlPullParserFactory

class ManifestParser(private val parserFactory: XmlPullParserFactory) {
    fun parseManifest(fileName: String): Result = try {
        Result.Data(Manifest.parse(fileName) { parserFactory.getXmlParser(it) ?: throw FileNotFoundException() })
    } catch (e: FileNotFoundException) {
        Result.Error.NotFound
    } catch (e: XmlPullParserException) {
        Result.Error.Corrupted
    } catch (e: Exception) {
        Result.Error()
    }
}

sealed class Result {
    class Data(val manifest: Manifest) : Result()

    open class Error : Result() {
        object Corrupted : Error()
        object NotFound : Error()
    }
}

private class FileNotFoundException : Exception()
