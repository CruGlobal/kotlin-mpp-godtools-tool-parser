package org.cru.godtools.shared.common.model

import co.touchlab.kermit.Logger
import platform.Foundation.NSURL

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Uri = NSURL
actual val Uri.scheme get() = scheme

actual fun String.toUriOrNull() = try {
    NSURL(string = this)
} catch (e: NullPointerException) {
    Logger.e(e) { "Error parsing URL '$this'" }
    null
}
