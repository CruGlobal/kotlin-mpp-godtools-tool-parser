@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.ccci.gto.android.common.compose.ui.draw.invisibleIf
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Visibility

@Composable
fun Modifier.visibility(model: Visibility, state: State): Modifier {
    val isInvisible by remember(model, state) {
        model.isInvisibleFlow(state)
    }.collectAsState(model.isInvisible(state))

    return invisibleIf(isInvisible)
}
