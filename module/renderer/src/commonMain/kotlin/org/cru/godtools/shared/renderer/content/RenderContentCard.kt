package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Card

@Composable
internal fun ColumnScope.RenderContentCard(
    card: Card,
    state: State,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(10.dp)
) {
    Column(
        modifier
            .background(card.backgroundColor.toComposeColor())
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        RenderContent(card.content, state)
    }
}
