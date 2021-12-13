package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.tool.model.Dimension.Pixels
import org.cru.godtools.tool.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_RESOURCE = "resource"
private const val XML_GRAVITY = "gravity"
private const val XML_WIDTH = "width"

class Image : Content, Clickable {
    internal companion object {
        internal const val XML_IMAGE = "image"

        private val DEFAULT_GRAVITY = Gravity.Horizontal.CENTER
        private val DEFAULT_WIDTH = Dimension.Percent(1f)
    }

    override val events: List<EventId>
    override val url: Uri?

    @VisibleForTesting
    internal val resourceName: String?
    val resource get() = getResource(resourceName)

    val gravity: Gravity.Horizontal
    val width: Dimension

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_IMAGE)

        resourceName = parser.getAttributeValue(XML_RESOURCE)
        parser.parseClickableAttrs { events, url ->
            this.events = events
            this.url = url
        }

        gravity = parser.getAttributeValue(XML_GRAVITY).toGravityOrNull()?.horizontal ?: DEFAULT_GRAVITY
        width = parser.getAttributeValue(XML_WIDTH).toDimensionOrNull()?.takeIf { it is Pixels } ?: DEFAULT_WIDTH

        parser.skipTag()
    }

    override val isIgnored get() = super.isIgnored || resourceName.isNullOrEmpty()

    @RestrictTo(RestrictToScope.TESTS)
    constructor(parent: Base, resource: String? = null) : super(parent) {
        resourceName = resource
        gravity = DEFAULT_GRAVITY
        width = DEFAULT_WIDTH
        events = emptyList()
        url = null
    }
}
