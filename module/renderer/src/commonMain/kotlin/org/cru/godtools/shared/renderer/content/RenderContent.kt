package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun ColumnScope.RenderContent(content: List<Content>) {
    content.forEach { RenderContent(it) }
}

@Composable
internal fun ColumnScope.RenderContent(content: Content) {
    when (content) {
        is Paragraph -> RenderParagraph(content)
        is Text -> RenderText(content)
        else -> Text(
            "Unsupported Content Element: ${content::class.simpleName}",
            color = Color.Red,
        )
    }
}
