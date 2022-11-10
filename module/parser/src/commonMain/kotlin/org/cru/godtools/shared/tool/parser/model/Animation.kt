package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_ANIMATION
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_RESOURCE = "resource"
private const val XML_LOOP = "loop"
private const val XML_AUTOPLAY = "autoplay"
private const val XML_PLAY_LISTENERS = "play-listeners"
private const val XML_STOP_LISTENERS = "stop-listeners"

class Animation : Content, Clickable {
    internal companion object {
        internal const val XML_ANIMATION = "animation"
    }

    @VisibleForTesting
    internal val resourceName: String?
    val resource get() = getResource(resourceName)

    val loop: Boolean
    val autoPlay: Boolean

    val playListeners: Set<EventId>
    val stopListeners: Set<EventId>

    override val events: List<EventId>
    override val url: Uri?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_ANIMATION)

        resourceName = parser.getAttributeValue(null, XML_RESOURCE)
        loop = parser.getAttributeValue(null, XML_LOOP)?.toBoolean() ?: true
        autoPlay = parser.getAttributeValue(null, XML_AUTOPLAY)?.toBoolean() ?: true

        playListeners = parser.getAttributeValue(XML_PLAY_LISTENERS).toEventIds().toSet()
        stopListeners = parser.getAttributeValue(XML_STOP_LISTENERS).toEventIds().toSet()

        parseClickableAttrs(parser) { events, url ->
            this.events = events
            this.url = url
        }

        parser.skipTag()
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(parent: Base = Manifest()) : super(parent) {
        resourceName = null
        loop = true
        autoPlay = true
        events = emptyList()
        url = null
        playListeners = emptySet()
        stopListeners = emptySet()
    }

    override val isIgnored get() = !manifest.config.supportsFeature(FEATURE_ANIMATION) || super.isIgnored
}
