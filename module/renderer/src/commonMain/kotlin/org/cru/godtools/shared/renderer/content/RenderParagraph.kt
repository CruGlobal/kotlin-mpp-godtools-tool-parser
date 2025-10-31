package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Paragraph

private val PARAGRAPH_VERTICAL_PADDING = 10.dp

@Composable
internal fun ColumnScope.RenderParagraph(paragraph: Paragraph, state: State) = Column(
    modifier = Modifier
        .visibility(paragraph, state)
        .padding(vertical = PARAGRAPH_VERTICAL_PADDING)
) {
    RenderContent(paragraph.content, state)
}
