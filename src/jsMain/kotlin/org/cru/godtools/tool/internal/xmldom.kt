@file:JsModule("xmldom")
@file:JsNonModule
package org.cru.godtools.tool.internal

import org.w3c.dom.Document

internal external class DOMParser {
    fun parseFromString(xmlsource: String, mimeType: String): Document
}
