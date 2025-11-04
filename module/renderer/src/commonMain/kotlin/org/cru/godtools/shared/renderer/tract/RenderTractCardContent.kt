package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.renderer.RenderBackground
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.content.RenderContent
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ProvideLayoutDirectionFromLocale
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.backgroundColor

@Composable
fun RenderTractCardContent(
    card: TractPage.Card,
    modifier: Modifier = Modifier,
    state: State = remember { State() }
) = ProvideLayoutDirectionFromLocale(card.manifest.locale) {
    Box(modifier) {
        RenderBackground(
            card.background.copy(color = card.backgroundColor.toSRGB().copy(alpha = 0f)),
            modifier = Modifier.matchParentSize(),
        )

        ProvideTextStyle(ToolTheme.ContentTextStyle) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                RenderContent(card.content, state)
            }
        }
    }
}
