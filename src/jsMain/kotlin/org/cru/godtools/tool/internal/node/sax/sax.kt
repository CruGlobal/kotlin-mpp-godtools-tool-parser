@file:JsModule("sax")
@file:JsNonModule
package org.cru.godtools.tool.internal.node.sax

open external class SAXParser(strict: Boolean, opt: SAXOptions) {
    open fun write(s: String): SAXParser
    open fun close(): SAXParser
    open fun onopentag(tag: QualifiedTag)
    open fun onclosetag(tagName: String)
}

external interface SAXOptions {
    val xmlns: Boolean?
}

external interface QualifiedName {
    val local: String
    val uri: String?
}

external interface QualifiedTag : QualifiedName {
    val attributes: Attributes
}

external interface QualifiedAttribute : QualifiedName {
    val value: String
}

external interface Attributes
