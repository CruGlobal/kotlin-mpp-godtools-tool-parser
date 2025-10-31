package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ProvideLayoutDirectionFromLocale
import org.cru.godtools.shared.tool.parser.model.Content

@Composable
fun RenderContentStack(
    content: List<Content>,
    modifier: Modifier = Modifier,
    state: State = remember { State() },
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    ProvideLayoutDirectionFromLocale(
        locale = content.asSequence()
            .mapNotNull { it.manifest.locale }
            .firstOrNull(),
    ) {
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            ProvideTextStyle(ToolTheme.ContentTextStyle) {
                RenderContent(content, state)
            }
        }
    }
}
