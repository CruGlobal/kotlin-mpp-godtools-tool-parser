package org.cru.godtools.shared.common.model

expect class Uri
expect val Uri.scheme: String?
val Uri.isHttpUrl: Boolean get() = scheme?.matches(Regex("https?", RegexOption.IGNORE_CASE)) == true

expect fun String?.toUriOrNull(): Uri?
