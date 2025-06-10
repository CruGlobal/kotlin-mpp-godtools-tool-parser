package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.content.extensions.invisibleIf
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Spacer

@Composable
internal fun ColumnScope.RenderSpacer(spacer: Spacer, state: State) {
    val modifier: Modifier = Modifier
        .invisibleIf(content = spacer, state = state)

    when (spacer.mode) {
        Spacer.Mode.FIXED -> Spacer(modifier.height(spacer.height.dp))
        Spacer.Mode.AUTO -> Spacer(modifier.weight(1.0f))
    }
}
