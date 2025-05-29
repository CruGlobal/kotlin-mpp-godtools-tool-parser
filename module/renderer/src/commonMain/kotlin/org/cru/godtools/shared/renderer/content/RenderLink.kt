package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.content.extensions.handleClickable
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Link

private val Link_Minimum_Height = 48.dp

@Composable
fun ColumnScope.RenderLink(link: Link, state: State, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .clickable { link.handleClickable(state, scope) }
            .fillMaxWidth()
            .heightIn(min = Link_Minimum_Height),
        contentAlignment = Alignment.Center
    ) {

        RenderTextNode(
            link.text,
            modifier = modifier
                .padding(horizontal = Horizontal_Padding)
                .fillMaxWidth()
        )
    }
}
