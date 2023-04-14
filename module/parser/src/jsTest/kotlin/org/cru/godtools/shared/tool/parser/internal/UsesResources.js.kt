package org.cru.godtools.shared.tool.parser.internal

import com.goncalossilva.resources.Resource
import org.cru.godtools.shared.tool.parser.xml.JsXmlPullParserFactory
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory
import kotlin.js.Promise

// HACK: this is currently hardcoded, hopefully at some point there will be a better way to access resources
private const val RESOURCES_ROOT = "src/commonTest/resources/org/cru/godtools/shared/tool/parser"

internal actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : JsXmlPullParserFactory() {
        override fun readFile(fileName: String) = try {
            val path = resourcesDir?.let { "$RESOURCES_ROOT/$it" } ?: RESOURCES_ROOT
            Promise.resolve(Resource("$path/$fileName").takeIf { it.exists() }?.readText())
        } catch (e: RuntimeException) {
            Promise.reject(e)
        }
    }
