package org.cru.godtools.shared.tool.parser.model

import io.github.aakira.napier.Napier
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.common.model.toUriOrNull
import org.cru.godtools.shared.tool.parser.internal.DeprecationException
import org.cru.godtools.shared.tool.parser.util.hasUriScheme
import org.cru.godtools.shared.tool.parser.util.toAbsoluteUriOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private const val XML_EVENTS = "events"
private const val XML_URL = "url"

interface Clickable : Base {
    val isClickable get() = url != null || events.isNotEmpty()

    val events: List<EventId>
    val url: Uri?
}

@OptIn(ExperimentalContracts::class)
internal inline fun Clickable.parseClickableAttrs(
    parser: XmlPullParser,
    block: (events: List<EventId>, url: Uri?) -> Unit
) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val rawUrl = parser.getAttributeValue(XML_URL)
    val uri = when {
        rawUrl?.hasUriScheme == false -> {
            val message = "Non-absolute uri tool: ${manifest.code} locale: ${manifest.locale} uri: $rawUrl"
            Napier.d(message, DeprecationException(message), "Uri")
            rawUrl.toAbsoluteUriOrNull()
        }
        else -> rawUrl.toUriOrNull()
    }

    block(parser.getAttributeValue(XML_EVENTS).toEventIds(), uri)
}
