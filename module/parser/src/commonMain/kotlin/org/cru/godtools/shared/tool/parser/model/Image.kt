@file:JvmMultifileClass
@file:JvmName("ImageKt")

package org.cru.godtools.shared.tool.parser.model

import co.touchlab.kermit.Logger
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.tool.parser.expressions.Expression
import org.cru.godtools.shared.tool.parser.internal.DeprecationException
import org.cru.godtools.shared.tool.parser.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.shared.tool.parser.model.Dimension.Pixels
import org.cru.godtools.shared.tool.parser.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

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
    @Suppress("ktlint:standard:blank-line-between-when-conditions")
    val resource
        get() = getResource(resourceName) ?: when {
            resourceName == null -> null
            manifest.config.legacyWebImageResources -> {
                val message = "tool: ${manifest.code} locale: ${manifest.locale} resource: $resourceName"
                Logger.e(message, DeprecationException("Legacy Manifest missing Image Resource $message"), "Image")
                Resource(manifest, resourceName)
            }
            else -> null
        }

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

    @Suppress("ktlint:standard:blank-line-between-when-conditions")
    override fun equals(other: Any?) = when {
        this === other -> true
        other == null -> false
        other !is Image -> false
        resource != other.resource -> false
        gravity != other.gravity -> false
        width != other.width -> false
        events != other.events -> false
        url != other.url -> false

        // TODO: these should be compared in a Content.equals() method once we add it.
        //       I'm waiting on adding it until more Content types implement equals
        invisibleIf != other.invisibleIf -> false
        goneIf != other.goneIf -> false
        else -> true
    }

    override fun hashCode(): Int {
        var result = events.hashCode()
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (resource?.hashCode() ?: 0)
        result = 31 * result + gravity.hashCode()
        result = 31 * result + width.hashCode()
        return result
    }

    @RestrictTo(RestrictToScope.TESTS)
    @JsName("createTestImage")
    constructor(
        parent: Base = Manifest(),
        resource: String? = null,
        gravity: Gravity.Horizontal = DEFAULT_GRAVITY,
        width: Dimension = DEFAULT_WIDTH,
        events: List<EventId> = emptyList(),
        url: Uri? = null,
        invisibleIf: Expression? = null,
        goneIf: Expression? = null,
    ) : super(parent, invisibleIf = invisibleIf, goneIf = goneIf) {
        resourceName = resource
        this.gravity = gravity
        this.width = width
        this.events = events
        this.url = url
    }
}
