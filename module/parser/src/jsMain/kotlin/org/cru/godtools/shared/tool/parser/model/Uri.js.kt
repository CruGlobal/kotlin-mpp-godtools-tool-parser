package org.cru.godtools.shared.tool.parser.model

actual typealias Uri = String
internal actual inline val Uri.scheme: String? get() = if (contains(":")) substringBefore(":") else null

internal actual fun String?.toUriOrNull() = this
