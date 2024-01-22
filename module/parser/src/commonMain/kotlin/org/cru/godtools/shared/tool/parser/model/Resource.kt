package org.cru.godtools.shared.tool.parser.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_FILENAME = "filename"
private const val XML_SRC = "src"

@JsExport
@OptIn(ExperimentalJsExport::class)
class Resource : BaseModel {
    internal companion object {
        internal const val XML_RESOURCE = "resource"
    }

    val name: String?
    val localName: String?

    internal constructor(manifest: Manifest, parser: XmlPullParser) : super(manifest) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_RESOURCE)

        name = parser.getAttributeValue(null, XML_FILENAME)
        localName = parser.getAttributeValue(null, XML_SRC)

        parser.skipTag()
    }

    internal constructor(
        manifest: Manifest = Manifest(),
        name: String? = null,
        localName: String? = null,
    ) : super(manifest) {
        this.name = name
        this.localName = localName
    }

    override fun equals(other: Any?) = when {
        this === other -> true
        other == null -> false
        other !is Resource -> false
        name != other.name -> false
        localName != other.localName -> false
        else -> true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (localName?.hashCode() ?: 0)
        return result
    }
}
