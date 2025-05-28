package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import org.cru.godtools.shared.tool.parser.model.Link

@Composable
internal fun ColumnScope.RenderLink(link: Link) {
    RenderText(link.text)
}
