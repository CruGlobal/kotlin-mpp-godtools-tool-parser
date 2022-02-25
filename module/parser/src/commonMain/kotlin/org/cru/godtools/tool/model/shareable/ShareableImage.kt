package org.cru.godtools.tool.model.shareable

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.getResource
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_RESOURCE = "resource"

class ShareableImage : Shareable {
    internal companion object {
        const val XML_IMAGE = "image"
    }

    override val id get() = super.id ?: resourceName

    private val resourceName: String?
    val resource get() = getResource(resourceName)

    override val description: Text?

    internal constructor(manifest: Manifest, parser: XmlPullParser) : super(manifest, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_SHAREABLE, XML_IMAGE)

        resourceName = parser.getAttributeValue(XML_RESOURCE)

        var description: Text? = null
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_SHAREABLE -> when (parser.name) {
                    XML_DESCRIPTION -> description = parser.parseTextChild(this, XMLNS_SHAREABLE, XML_DESCRIPTION)
                }
            }
        }
        this.description = description
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(manifest: Manifest = Manifest(), id: String? = null) : super(manifest, id) {
        resourceName = null
        description = null
    }
}
