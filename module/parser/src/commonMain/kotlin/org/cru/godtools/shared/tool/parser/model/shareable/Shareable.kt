package org.cru.godtools.shared.tool.parser.model.shareable

import org.cru.godtools.shared.tool.parser.internal.RestrictTo
import org.cru.godtools.shared.tool.parser.internal.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.Base
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_ID = "id"
private const val XML_ORDER = "order"

private const val DEFAULT_ORDER = Int.MAX_VALUE

sealed class Shareable : BaseModel {
    internal companion object {
        internal const val XML_ITEMS = "items"

        fun XmlPullParser.parseShareableItems(manifest: Manifest) = buildList {
            require(XmlPullParser.START_TAG, XMLNS_SHAREABLE, XML_ITEMS)
            parseChildren {
                when (namespace) {
                    XMLNS_SHAREABLE -> parse(manifest, this@parseShareableItems)?.let { add(it) }
                }
            }
        }

        internal fun parse(manifest: Manifest, parser: XmlPullParser): Shareable? {
            parser.require(XmlPullParser.START_TAG, XMLNS_SHAREABLE)

            return when (parser.namespace) {
                XMLNS_SHAREABLE -> when (parser.name) {
                    ShareableImage.XML_IMAGE -> ShareableImage(manifest, parser)
                    else -> null
                }
                else -> null
            }
        }
    }

    open val id: String?
    val order: Int

    constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        parser.require(XmlPullParser.START_TAG, XMLNS_SHAREABLE)
        id = parser.getAttributeValue(XML_ID)
        order = parser.getAttributeValue(XML_ORDER)?.toIntOrNull() ?: DEFAULT_ORDER
    }

    @RestrictTo(RestrictToScope.TESTS)
    constructor(parent: Base, id: String? = null) : super(parent) {
        this.id = id
        order = DEFAULT_ORDER
    }
}
