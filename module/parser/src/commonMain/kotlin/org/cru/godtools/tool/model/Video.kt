package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.AspectRatio.Companion.toAspectRatioOrNull
import org.cru.godtools.tool.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.tool.model.Dimension.Pixels
import org.cru.godtools.tool.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_VIDEO_ID = "video-id"
private const val XML_PROVIDER = "provider"
private const val XML_PROVIDER_YOUTUBE = "youtube"
private const val XML_ASPECT_RATIO = "aspect-ratio"
private const val XML_GRAVITY = "gravity"
private const val XML_WIDTH = "width"

class Video : Content {
    internal companion object {
        internal const val XML_VIDEO = "video"

        internal val DEFAULT_ASPECT_RATIO = AspectRatio(16, 9)
        internal val DEFAULT_GRAVITY = Gravity.Horizontal.CENTER
        internal val DEFAULT_WIDTH = Dimension.Percent(1f)
    }

    enum class Provider {
        YOUTUBE, UNKNOWN;

        internal companion object {
            internal val DEFAULT = UNKNOWN

            internal fun parseOrNull(value: String?) = when (value) {
                XML_PROVIDER_YOUTUBE -> YOUTUBE
                else -> null
            }
        }
    }

    val provider: Provider
    val videoId: String?

    val aspectRatio: AspectRatio
    val gravity: Gravity.Horizontal
    val width: Dimension

    override val isIgnored get() = super.isIgnored || provider == Provider.UNKNOWN || videoId == null

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_VIDEO)

        provider = Provider.parseOrNull(parser.getAttributeValue(XML_PROVIDER)) ?: Provider.DEFAULT
        videoId = parser.getAttributeValue(XML_VIDEO_ID)

        aspectRatio = parser.getAttributeValue(XML_ASPECT_RATIO).toAspectRatioOrNull() ?: DEFAULT_ASPECT_RATIO
        gravity = parser.getAttributeValue(XML_GRAVITY).toGravityOrNull()?.horizontal ?: DEFAULT_GRAVITY
        width = parser.getAttributeValue(XML_WIDTH).toDimensionOrNull()?.takeIf { it is Pixels } ?: DEFAULT_WIDTH

        parser.skipTag()
    }
}
