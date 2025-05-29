package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Link
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun ColumnScope.RenderContent(content: List<Content>, state: State) {
    content.forEach { RenderContent(it, state) }
}

@Composable
internal fun ColumnScope.RenderContent(content: Content, state: State) {
    when (content) {
        is Button -> RenderButton(content, state)
        is Paragraph -> RenderParagraph(content, state)
        is Text -> RenderText(content)
        is Link -> RenderLink(content)
        is Spacer -> RenderSpacer(content)
        else -> Text(
            "Unsupported Content Element: ${content::class.simpleName}",
            color = Color.Red,
        )
    }
}
