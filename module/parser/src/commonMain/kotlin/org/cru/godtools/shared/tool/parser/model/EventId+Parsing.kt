@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.util.REGEX_SEQUENCE_SEPARATOR

internal fun String.toEventIds() = split(REGEX_SEQUENCE_SEPARATOR)
    .mapNotNull {
        val components = it.split(':', limit = 2)
        when {
            it.isBlank() -> null
            components.size == 1 -> EventId(name = it)
            else -> EventId(components[0], components[1])
        }
    }
