package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.tool.parser.model.Spacer

@Composable
internal fun RenderSpacer(spacer: Spacer, modifier: Modifier = Modifier) {

    when (spacer.mode) {
        Spacer.Mode.FIXED -> Spacer(Modifier.height(spacer.height.dp))
        Spacer.Mode.AUTO -> Column { Spacer(modifier = modifier.weight(1.0f)) }
    }
}
