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
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC

private const val XML_EVENTS = "events"
private const val XML_URL = "url"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
interface Clickable : Base {
    val isClickable get() = url != null || events.isNotEmpty()

    @JsName("_events")
    val events: List<EventId>
    val url: Uri?

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("events")
    val jsEvents get() = events.toTypedArray()
    // endregion Kotlin/JS interop
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
