package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Animation
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Card
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Flow
import org.cru.godtools.shared.tool.parser.model.Form
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Input
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
    val isGone by remember(content, state) { content.isGoneFlow(state) }.collectAsState(content.isGone(state))

    if (isGone) return

    when (content) {
        is Animation -> RenderAnimation(content, state)
        is Button -> RenderButton(content, state)
        is Card -> RenderContentCard(content, state)
        is Flow -> RenderFlow(content, state)
        is Form -> RenderForm(content, state)
        is Image -> RenderImage(content, state)
        is Input -> RenderInput(content, state)
        is Paragraph -> RenderParagraph(content, state)
        is Text -> RenderText(content, state)
        is Link -> RenderLink(content, state)
        is Spacer -> RenderSpacer(content, state)
        else -> Text(
            "Unsupported Content Element: ${content::class.simpleName}",
            color = Color.Red,
        )
    }
}
