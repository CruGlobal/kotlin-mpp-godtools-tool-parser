package org.cru.godtools.tool.model

expect class Uri

internal expect fun String.toUri(): Uri

internal fun String.toAbsoluteUri(defaultScheme: String = "http"): Uri = makeAbsolute(defaultScheme).toUri()
private fun String.makeAbsolute(defaultScheme: String) = if (this.contains(':')) this else "$defaultScheme://$this"
