package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_FILENAME = "filename"
private const val XML_SRC = "src"
private const val XML_CHECKSUM_SHA256 = "checksum-sha256"
private const val XML_SIZE = "size"

@JsExport
@OptIn(ExperimentalJsExport::class)
class Resource : BaseModel {
    internal companion object {
        internal const val XML_RESOURCE = "resource"
    }

    val name: String?
    val localName: String?
    val checksumSha256: String?
    val size: Int?

    internal constructor(manifest: Manifest, parser: XmlPullParser) : super(manifest) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_RESOURCE)

        name = parser.getAttributeValue(XML_FILENAME)
        localName = parser.getAttributeValue(XML_SRC)
        checksumSha256 = parser.getAttributeValue(XML_CHECKSUM_SHA256)?.lowercase()
        size = parser.getAttributeValue(XML_SIZE)?.toIntOrNull()

        parser.skipTag()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    @JsName("createTestResource")
    constructor(
        manifest: Manifest = Manifest(),
        name: String? = null,
        localName: String? = null,
        checksumSha256: String? = null,
        size: Int? = null,
    ) : super(manifest) {
        this.name = name
        this.localName = localName
        this.checksumSha256 = checksumSha256
        this.size = size
    }

    override fun equals(other: Any?) = when {
        this === other -> true
        other == null -> false
        other !is Resource -> false
        name != other.name -> false
        localName != other.localName -> false
        checksumSha256 != other.checksumSha256 -> false
        size != other.size -> false
        else -> true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (localName?.hashCode() ?: 0)
        result = 31 * result + (checksumSha256?.hashCode() ?: 0)
        result = 31 * result + (size?.hashCode() ?: 0)
        return result
    }
}
