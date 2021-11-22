package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private const val XML_URL = "url"

interface Clickable : Base {
    val isClickable get() = url != null || events.isNotEmpty()

    val events: List<EventId>
    val url: Uri?
}

@OptIn(ExperimentalContracts::class)
internal inline fun XmlPullParser.parseClickableAttrs(block: (events: List<EventId>, url: Uri?) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(getAttributeValue(XML_EVENTS).toEventIds(), getAttributeValue(XML_URL).toAbsoluteUriOrNull())
}
