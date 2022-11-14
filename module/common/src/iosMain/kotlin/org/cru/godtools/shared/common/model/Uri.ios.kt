package org.cru.godtools.shared.common.model

import io.github.aakira.napier.Napier
import platform.Foundation.NSURL

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Uri = NSURL

actual fun String?.toUriOrNull() = this?.let {
    try {
        NSURL(string = it)
    } catch (e: NullPointerException) {
        Napier.e("Error parsing URL '$it'", e)
        null
    }
}
