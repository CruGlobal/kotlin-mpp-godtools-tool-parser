package org.cru.godtools.tool.internal

import okio.ExperimentalFileSystem
import okio.FileNotFoundException
import okio.NodeJsFileSystem
import okio.Path.Companion.toPath
import okio.buffer
import org.cru.godtools.tool.xml.JsXmlPullParserFactory
import org.cru.godtools.tool.xml.XmlPullParserFactory

// HACK: this is currently hardcoded, hopefully at some point there will be a better way to access resources
@OptIn(ExperimentalFileSystem::class)
private val RESOURCES_ROOT = "resources".toPath()

@OptIn(ExperimentalFileSystem::class)
internal actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : JsXmlPullParserFactory() {
        override fun readFile(fileName: String) = try {
            val path = RESOURCES_ROOT / "org/cru/godtools/tool" / resourcesDir
            NodeJsFileSystem.source(path / fileName).buffer().readUtf8()
        } catch (e: FileNotFoundException) {
            null
        }
    }

// region Android Robolectric
actual abstract class Runner
actual class AndroidJUnit4 : Runner()
// endregion Android Robolectric
