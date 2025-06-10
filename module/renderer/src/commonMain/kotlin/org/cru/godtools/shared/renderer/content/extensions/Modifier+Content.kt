@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Visibility

@Composable
fun Modifier.content(content: Content, state: State) {
    Modifier
        .invisibleIf(invisible = (content as? Visibility)?.isInvisibleFlow(state = state)?.collectAsState(false)?.value ?: false)
}
