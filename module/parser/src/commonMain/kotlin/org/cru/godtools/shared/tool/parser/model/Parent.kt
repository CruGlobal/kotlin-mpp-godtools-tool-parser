package org.cru.godtools.shared.tool.parser.model

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.tool.parser.model.Content.Companion.parseContentElement
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
interface Parent : Base {
    @JsName("_content")
    val content: List<Content>

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("content")
    val jsContent get() = content.toTypedArray()
    // endregion Kotlin/JS interop
}

internal inline val Parent.contentTips get() = content.flatMap { it.tips }

/**
 * @param block Custom parsing logic, if the block processes the current tag,
 * it should advance the parser to the END_TAG event.
 */
internal inline fun Parent.parseContent(
    parser: XmlPullParser,
    block: () -> Unit = { }
) = buildList {
    parser.require(XmlPullParser.START_TAG, null, null)
    parser.parseChildren {
        // execute any custom parsing logic from the call-site
        // if the block consumes the tag, the parser will be on an END_TAG after returning
        block()
        if (parser.eventType == XmlPullParser.END_TAG) return@parseChildren

        parser.parseContentElement(this@parseContent)?.takeUnless { it.isIgnored }?.let { add(it) }
    }
}
