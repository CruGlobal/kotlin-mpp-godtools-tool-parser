package org.cru.godtools.shared.common.model

// TODO: switch from String to URL
actual typealias Uri = String

actual fun String?.toUriOrNull() = this
