package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.tool.parser.model.Link

@Composable
fun ColumnScope.RenderLink(link: Link, modifier: Modifier = Modifier) {
    RenderTextNode(
        link.text,
        modifier = modifier
            .padding(horizontal = Horizontal_Padding)
            .fillMaxWidth()
            .clickable { /* TODO */ }
    )
}
