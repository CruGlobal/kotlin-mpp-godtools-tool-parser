@file:JvmMultifileClass
@file:JvmName("ImageKt")

package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.tool.parser.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.shared.tool.parser.model.Dimension.Pixels
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

private const val XML_RESOURCE = "resource"
private const val XML_GRAVITY = "gravity"
private const val XML_WIDTH = "width"

@JsExport
@OptIn(ExperimentalJsExport::class)
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
    val resource get() = manifest.getResource(resourceName)

    val gravity: Gravity.Horizontal
    val width: Dimension

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
    @JsName("createTestImage")
    constructor(
        parent: Base = Manifest(),
        resource: String? = null,
        gravity: Gravity.Horizontal = DEFAULT_GRAVITY,
        width: Dimension = DEFAULT_WIDTH,
    ) : super(parent) {
        resourceName = resource
        this.gravity = gravity
        this.width = width
        events = emptyList()
        url = null
    }
}
