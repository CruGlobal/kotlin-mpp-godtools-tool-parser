package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_RESOURCE = "resource"

class Image : Content, Clickable {
    internal companion object {
        internal const val XML_IMAGE = "image"
    }

    override val events: List<EventId>
    override val url: Uri?

    @VisibleForTesting
    internal val resourceName: String?
    val resource get() = getResource(resourceName)

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_IMAGE)

        resourceName = parser.getAttributeValue(XML_RESOURCE)
        parser.parseClickableAttrs { events, url ->
            this.events = events
            this.url = url
        }

        parser.skipTag()
    }

    override val isIgnored get() = super.isIgnored || resourceName.isNullOrEmpty()

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(parent: Base, resource: String? = null) : super(parent) {
        resourceName = resource
        events = emptyList()
        url = null
    }
}
