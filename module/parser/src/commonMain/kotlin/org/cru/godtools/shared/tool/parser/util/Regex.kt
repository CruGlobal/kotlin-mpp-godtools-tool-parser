package org.cru.godtools.shared.tool.parser.util

internal val REGEX_SEQUENCE_SEPARATOR = Regex("\\s+")

internal operator fun Regex.contains(value: String) = matches(value)
