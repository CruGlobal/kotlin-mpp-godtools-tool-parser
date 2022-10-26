package org.cru.godtools.shared.tool.parser.model

expect class Uri
internal expect val Uri.scheme: String?
internal val Uri.isHttpUrl: Boolean get() = scheme?.matches(Regex("https?", RegexOption.IGNORE_CASE)) == true

internal expect fun String?.toUriOrNull(): Uri?

internal fun String?.toAbsoluteUriOrNull(defaultScheme: String = "http") =
    this?.addSchemeIfNecessary(defaultScheme).toUriOrNull()
internal val String.hasUriScheme get() = contains(':')
private fun String.addSchemeIfNecessary(defaultScheme: String) = if (hasUriScheme) this else "$defaultScheme://$this"
