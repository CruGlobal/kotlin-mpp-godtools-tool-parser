package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_VIDEO_ID = "video-id"
private const val XML_PROVIDER = "provider"
private const val XML_PROVIDER_YOUTUBE = "youtube"

class Video : Content {
    internal companion object {
        internal const val XML_VIDEO = "video"
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

    override val isIgnored get() = super.isIgnored || provider == Provider.UNKNOWN || videoId == null

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_VIDEO)

        provider = Provider.parseOrNull(parser.getAttributeValue(null, XML_PROVIDER)) ?: Provider.DEFAULT
        videoId = parser.getAttributeValue(null, XML_VIDEO_ID)

        parser.skipTag()
    }
}
