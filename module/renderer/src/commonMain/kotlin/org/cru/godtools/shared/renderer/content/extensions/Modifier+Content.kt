package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Visibility
import org.cru.godtools.shared.tool.parser.model.VisibilityEnum

fun Modifier.content(content: Content, state: State) =
    composed(
        factory = {
            val visibility: VisibilityEnum = (content as? Visibility)?.let { it
                it.getVisibility(state = state)
            } ?: run {
                VisibilityEnum.VISIBLE
            }

            Modifier
                .visibility(visibility)
        }
    )
