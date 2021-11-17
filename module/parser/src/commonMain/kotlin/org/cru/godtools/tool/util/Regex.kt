package org.cru.godtools.tool.util

internal operator fun Regex.contains(value: String) = matches(value)
