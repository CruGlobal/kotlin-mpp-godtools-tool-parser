package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Card

@Composable
internal fun RenderContentCard(
    card: Card,
    state: State,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(10.dp)
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = card.backgroundColor.toComposeColor(),
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Column(
            modifier = modifier
        ) {
            RenderContent(card.content, state)
        }
    }
}
