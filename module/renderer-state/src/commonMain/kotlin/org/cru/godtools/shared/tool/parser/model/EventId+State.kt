@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.renderer.state.State

fun EventId.resolve(state: State) = state.resolveContentEvent(this)
