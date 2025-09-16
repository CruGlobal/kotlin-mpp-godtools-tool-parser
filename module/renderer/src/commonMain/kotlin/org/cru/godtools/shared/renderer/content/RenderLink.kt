package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.clickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Link

@Composable
fun ColumnScope.RenderLink(link: Link, state: State, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    RenderTextNode(
        link.text,
        modifier = modifier
            .visibility(link, state)
            .padding(horizontal = ContentHorizontalPadding)
            .align(link.text.textAlign.alignment)
            .clickable(link, state, scope)
    )
}
