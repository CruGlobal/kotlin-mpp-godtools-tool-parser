package org.cru.godtools.tool.model

import platform.Foundation.NSURL

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Uri = NSURL
internal actual inline val Uri.scheme get() = scheme

internal actual fun String.toUri() = NSURL(string = this)
