package org.cru.godtools.shared.tool.parser.model.shareable

import androidx.annotation.RestrictTo
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.getResource
import org.cru.godtools.shared.tool.parser.model.parseTextChild
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_DESCRIPTION = "description"
private const val XML_RESOURCE = "resource"

class ShareableImage : Shareable {
    internal companion object {
        const val XML_IMAGE = "image"
    }

    override val id get() = super.id ?: resourceName

    private val resourceName: String?
    val resource get() = getResource(resourceName)

    val description: Text?

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

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        manifest: Manifest = Manifest(),
        id: String? = null,
        resource: String? = null
    ) : super(manifest, id) {
        resourceName = resource
        description = null
    }
}
