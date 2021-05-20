@file:JsModule("sax")
@file:JsNonModule
package org.cru.godtools.tool.internal.node.sax

open external class SAXParser(strict: Boolean, opt: SAXOptions) {
    open fun write(s: String): SAXParser
    open fun close(): SAXParser
    open fun onopentag(tag: QualifiedTag)
}

external interface SAXOptions {
    val xmlns: Boolean?
}

external interface QualifiedName {
    val name: String
    val uri: String
}

external interface QualifiedTag : QualifiedName
