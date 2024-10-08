package org.cru.godtools.shared.common.model

// TODO: switch from String to URL
actual typealias Uri = String
actual val Uri.scheme get() = if (contains(":")) substringBefore(":") else null

actual fun String?.toUriOrNull() = this
