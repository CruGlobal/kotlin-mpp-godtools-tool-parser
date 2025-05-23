@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.renderer.state.State

fun EventId.resolve(state: State) = when (namespace) {
    EventId.NAMESPACE_STATE -> state.getVar(name).map { EventId(name = it) }
    else -> listOf(this)
}
