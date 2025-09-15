package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.handleClickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Card

@Composable
internal fun RenderContentCard(card: Card, state: State, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    val invisible by remember(card, state) {
        card.isInvisibleFlow(state)
    }.collectAsState(card.isInvisible(state))

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = card.backgroundColor.toComposeColor(),
        ),
        onClick = { card.handleClickable(state, scope) },
        enabled = !invisible,
        modifier = modifier
            .visibility(card, state)
            .fillMaxWidth()
            .padding(CardPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            RenderContent(card.content, state)
        }
    }
}
