package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.clickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Card

@Composable
internal fun RenderContentCard(
    card: Card,
    state: State,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = card.backgroundColor.toComposeColor(),
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .visibility(card, state)
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(card, state, scope)
    ) {
        Column(
            modifier = Modifier
        ) {
            RenderContent(card.content, state)
        }
    }
}
