package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.Content.Companion.parseContentElement
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

interface Parent : Base {
    val content: List<Content>
}

internal inline val Parent.contentTips get() = content.flatMap { it.tips }

/**
 * @param block Custom parsing logic, if the block processes the current tag,
 * it should advance the parser to the END_TAG event.
 */
@OptIn(ExperimentalStdlibApi::class)
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
