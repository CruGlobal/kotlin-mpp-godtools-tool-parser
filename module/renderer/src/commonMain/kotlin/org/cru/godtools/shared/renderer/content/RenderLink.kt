package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.handleClickable
import org.cru.godtools.shared.renderer.content.extensions.textAlign
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Link

@Composable
fun ColumnScope.RenderLink(link: Link, state: State, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    RenderTextNode(
        link.text,
        modifier = modifier
            .padding(horizontal = Horizontal_Padding)
            .minimumInteractiveComponentSize()
            .align(link.text.textAlign.textAlign.alignment)
            .clickable { link.handleClickable(state, scope) }
    )
}
