package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.tool.parser.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.shared.tool.parser.model.Dimension.Pixels
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_RESOURCE = "resource"
private const val XML_GRAVITY = "gravity"
private const val XML_WIDTH = "width"

class Image : Content, Clickable {
    internal companion object {
        internal const val XML_IMAGE = "image"

        internal val DEFAULT_GRAVITY = Gravity.Horizontal.CENTER
        internal val DEFAULT_WIDTH = Dimension.Percent(1f)
    }

    override val events: List<EventId>
    override val url: Uri?

    @VisibleForTesting
    internal val resourceName: String?
    val resource get() = getResource(resourceName)

    internal val gravity: Gravity.Horizontal
    internal val width: Dimension

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_IMAGE)

        resourceName = parser.getAttributeValue(XML_RESOURCE)
        parseClickableAttrs(parser) { events, url ->
            this.events = events
            this.url = url
        }

        gravity = parser.getAttributeValue(XML_GRAVITY).toGravityOrNull()?.horizontal ?: DEFAULT_GRAVITY
        width = parser.getAttributeValue(XML_WIDTH).toDimensionOrNull()?.takeIf { it is Pixels } ?: DEFAULT_WIDTH

        parser.skipTag()
    }

    override val isIgnored get() = super.isIgnored || resourceName.isNullOrEmpty()

    @RestrictTo(RestrictToScope.TESTS)
    constructor(
        parent: Base = Manifest(),
        resource: String? = null,
        gravity: Gravity.Horizontal = DEFAULT_GRAVITY,
        width: Dimension = DEFAULT_WIDTH
    ) : super(parent) {
        resourceName = resource
        this.gravity = gravity
        this.width = width
        events = emptyList()
        url = null
    }
}

val Image?.gravity get() = this?.gravity ?: Image.DEFAULT_GRAVITY
val Image?.width get() = this?.width ?: Image.DEFAULT_WIDTH
