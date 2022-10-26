package org.cru.godtools.shared.tool.parser.internal

import okio.FileNotFoundException
import okio.NodeJsFileSystem
import okio.Path.Companion.toPath
import okio.buffer
import org.cru.godtools.shared.tool.parser.xml.JsXmlPullParserFactory
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory

// HACK: this is currently hardcoded, hopefully at some point there will be a better way to access resources
private val RESOURCES_ROOT = "resources".toPath()

internal actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : JsXmlPullParserFactory() {
        override fun readFile(fileName: String) = try {
            val basePath = RESOURCES_ROOT / "org/cru/godtools/shared/tool/parser"
            val path = resourcesDir?.let { basePath / it } ?: basePath
            NodeJsFileSystem.source(path / fileName).buffer().readUtf8()
        } catch (e: FileNotFoundException) {
            null
        }
    }
