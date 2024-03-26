package org.cru.godtools.shared.common.model

import co.touchlab.kermit.Logger
import platform.Foundation.NSURL

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Uri = NSURL

actual fun String?.toUriOrNull() = this?.let {
    try {
        NSURL(string = it)
    } catch (e: NullPointerException) {
        Logger.e(e) { "Error parsing URL '$it'" }
        null
    }
}
