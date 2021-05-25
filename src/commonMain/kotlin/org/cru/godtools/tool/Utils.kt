package org.cru.godtools.tool

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
internal val REGEX_SEQUENCE_SEPARATOR = Regex("\\s+")
