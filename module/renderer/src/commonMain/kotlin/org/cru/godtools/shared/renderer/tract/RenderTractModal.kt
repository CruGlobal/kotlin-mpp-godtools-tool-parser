package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.RenderContent
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ContentEventListener
import org.cru.godtools.shared.renderer.util.ProvideLayoutDirectionFromLocale
import org.cru.godtools.shared.tool.parser.model.tract.Modal

@Composable
fun RenderTractModal(modal: Modal, state: State, modifier: Modifier = Modifier, onDismiss: () -> Unit = {}) {
    ContentEventListener(state, modal) { event ->
        if (event in modal.dismissListeners) onDismiss()
    }

    ProvideLayoutDirectionFromLocale(modal.manifest.locale) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            modal.title?.let {
                ProvideTextStyle(MaterialTheme.typography.displayMedium) {
                    RenderTextNode(
                        it,
                        modifier = Modifier
                            .padding(horizontal = ContentHorizontalPadding)
                            .fillMaxWidth()
                    )
                }
            }
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                RenderContent(modal.content, state)
            }
        }
    }
}
