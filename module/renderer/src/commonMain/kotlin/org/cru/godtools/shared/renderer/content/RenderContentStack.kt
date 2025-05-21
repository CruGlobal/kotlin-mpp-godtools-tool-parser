package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.tool.parser.model.Content

@Composable
fun RenderContentStack(content: List<Content>, modifier: Modifier = Modifier) = Column(modifier) {
    RenderContent(content)
}
