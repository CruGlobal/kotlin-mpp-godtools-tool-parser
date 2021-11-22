package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import platform.Foundation.NSURL

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Uri = NSURL
internal actual inline val Uri.scheme get() = scheme

internal actual fun String?.toUriOrNull() = this?.let {
    try {
        NSURL(string = it)
    } catch (e: NullPointerException) {
        Napier.e("Error parsing URL '$it'", e)
        null
    }
}
