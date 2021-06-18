package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_RESOURCE = "resource"

class Image : Content {
    internal companion object {
        internal const val XML_IMAGE = "image"
    }

    val events: List<EventId>

    @VisibleForTesting
    internal val resourceName: String?
    val resource get() = getResource(resourceName)

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_IMAGE)

        resourceName = parser.getAttributeValue(XML_RESOURCE)
        events = parser.getAttributeValue(XML_EVENTS)?.toEventIds().orEmpty()

        parser.skipTag()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(parent: Base, resource: String? = null) : super(parent) {
        resourceName = resource
        events = emptyList()
    }
}
