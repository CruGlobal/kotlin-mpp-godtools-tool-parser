package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import org.cru.godtools.shared.tool.parser.model.VisibilityEnum

fun Modifier.visibility(visibility: VisibilityEnum) =
    composed(
        factory = {
            when (visibility) {
                VisibilityEnum.GONE -> Modifier
                VisibilityEnum.INVISIBLE -> Modifier.alpha(0f)
                VisibilityEnum.VISIBLE -> Modifier.alpha(1f)
            }
        }
    )
