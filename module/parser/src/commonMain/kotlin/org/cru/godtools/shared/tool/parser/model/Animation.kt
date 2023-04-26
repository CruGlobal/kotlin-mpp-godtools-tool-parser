package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_ANIMATION
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC

private const val XML_RESOURCE = "resource"
private const val XML_LOOP = "loop"
private const val XML_AUTOPLAY = "autoplay"
private const val XML_PLAY_LISTENERS = "play-listeners"
private const val XML_STOP_LISTENERS = "stop-listeners"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class Animation : Content, Clickable {
    internal companion object {
        private const val CONTENT_TYPE = "animation"
        internal const val XML_ANIMATION = "animation"
    }

    @VisibleForTesting
    internal val resourceName: String?
    val resource get() = getResource(resourceName)

    val loop: Boolean
    val autoPlay: Boolean

    @JsName("_playListeners")
    val playListeners: Set<EventId>
    @JsName("_stopListeners")
    val stopListeners: Set<EventId>

    override val events: List<EventId>
    override val url: Uri?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, CONTENT_TYPE, parser) {
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
    internal constructor(parent: Base = Manifest()) : super(parent, CONTENT_TYPE) {
        resourceName = null
        loop = true
        autoPlay = true
        events = emptyList()
        url = null
        playListeners = emptySet()
        stopListeners = emptySet()
    }

    override val isIgnored get() = !manifest.config.supportsFeature(FEATURE_ANIMATION) || super.isIgnored

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("playListeners")
    val jsPlayListeners get() = playListeners.toTypedArray()
    @HiddenFromObjC
    @JsName("stopListeners")
    val jsStopListeners get() = stopListeners.toTypedArray()
    // endregion Kotlin/JS interop
}
