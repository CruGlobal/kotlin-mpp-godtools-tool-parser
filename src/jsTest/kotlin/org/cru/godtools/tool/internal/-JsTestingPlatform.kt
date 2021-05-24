package org.cru.godtools.tool.internal

import okio.ExperimentalFileSystem
import okio.NodeJsFileSystem
import okio.Path.Companion.DIRECTORY_SEPARATOR
import okio.Path.Companion.toPath
import okio.buffer
import org.cru.godtools.tool.xml.JsXmlPullParserFactory
import org.cru.godtools.tool.xml.XmlPullParserFactory

// HACK: this is currently hardcoded, hopefully at some point there will be a better way to access resources
@OptIn(ExperimentalFileSystem::class)
private val RESOURCES_ROOT = "..$DIRECTORY_SEPARATOR../../processedResources/js/test".toPath()

@OptIn(ExperimentalFileSystem::class)
actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : JsXmlPullParserFactory() {
        override fun readFile(fileName: String): String {
            var path = RESOURCES_ROOT / "org/cru/godtools/tool"
            resourcesDir?.let { path = path / it }
            return NodeJsFileSystem.source(path / fileName).buffer().readUtf8()
        }
    }

// region Android Robolectric
actual abstract class Runner
actual class AndroidJUnit4 : Runner()
// endregion Android Robolectric
