package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.tool.parser.model.Paragraph

private val PARAGRAPH_VERTICAL_PADDING = 10.dp

@Composable
internal fun ColumnScope.RenderParagraph(paragraph: Paragraph) {
    Spacer(Modifier.height(PARAGRAPH_VERTICAL_PADDING))
    RenderContent(paragraph.content)
    Spacer(Modifier.height(PARAGRAPH_VERTICAL_PADDING))
}
