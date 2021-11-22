package org.cru.godtools.tool.model

expect class Uri
internal expect val Uri.scheme: String?
internal val Uri.isHttpUrl: Boolean get() = scheme?.matches(Regex("https?", RegexOption.IGNORE_CASE)) == true

internal expect fun String.toUri(): Uri

internal fun String?.toAbsoluteUriOrNull(defaultScheme: String = "http") = this?.makeAbsolute(defaultScheme)?.toUri()
private fun String.makeAbsolute(defaultScheme: String) = if (this.contains(':')) this else "$defaultScheme://$this"
