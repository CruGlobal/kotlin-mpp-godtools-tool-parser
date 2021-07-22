package org.cru.godtools.tool.model

import org.cru.godtools.tool.FEATURE_ANIMATION
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_RESOURCE = "resource"
private const val XML_LOOP = "loop"
private const val XML_AUTOPLAY = "autoplay"
private const val XML_PLAY_LISTENERS = "play-listeners"
private const val XML_STOP_LISTENERS = "stop-listeners"

class Animation : Content {
    internal companion object {
        internal const val XML_ANIMATION = "animation"
    }

    @VisibleForTesting
    internal val resourceName: String?
    val resource get() = getResource(resourceName)

    val loop: Boolean
    val autoPlay: Boolean

    val events: List<EventId>
    val playListeners: Set<EventId>
    val stopListeners: Set<EventId>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_ANIMATION)

        resourceName = parser.getAttributeValue(null, XML_RESOURCE)
        loop = parser.getAttributeValue(null, XML_LOOP)?.toBoolean() ?: true
        autoPlay = parser.getAttributeValue(null, XML_AUTOPLAY)?.toBoolean() ?: true

        events = parser.getAttributeValue(XML_EVENTS).toEventIds()
        playListeners = parser.getAttributeValue(XML_PLAY_LISTENERS).toEventIds().toSet()
        stopListeners = parser.getAttributeValue(XML_STOP_LISTENERS).toEventIds().toSet()

        parser.skipTag()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor() : super(Manifest()) {
        resourceName = null
        loop = true
        autoPlay = true
        events = emptyList()
        playListeners = emptySet()
        stopListeners = emptySet()
    }

    override val isIgnored get() = FEATURE_ANIMATION !in ParserConfig.supportedFeatures || super.isIgnored
}
