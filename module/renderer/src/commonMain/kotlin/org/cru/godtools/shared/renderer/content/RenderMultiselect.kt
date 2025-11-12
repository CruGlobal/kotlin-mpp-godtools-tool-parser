package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.ToolTheme.CardPadding
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Multiselect
import org.cru.godtools.shared.tool.parser.model.Multiselect.Option.Style

internal const val TestTagMultiselect = "multiselect"

@Composable
internal fun ColumnScope.RenderMultiselect(multiselect: Multiselect, state: State) {
    Column(
        modifier = Modifier
            .testTag(TestTagMultiselect)
            .visibility(multiselect, state)
    ) {
        multiselect.options.chunked(multiselect.columns).forEach { options ->
            Row(Modifier.fillMaxWidth()) {
                options.forEach { option ->
                    key(option.id) {
                        when (option.style) {
                            Style.CARD -> RenderMultiselectOptionCard(option, state, modifier = Modifier.weight(1f))
                            Style.FLAT -> RenderMultiselectOptionFlat(option, state, modifier = Modifier.weight(1f))
                        }
                    }
                }
                repeat(multiselect.columns - options.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RenderMultiselectOptionFlat(option: Multiselect.Option, state: State, modifier: Modifier = Modifier) {
    val isClickable by option.produceIsClickable(state)
    val isSelected by option.produceIsSelected(state)

    Surface(
        onClick = { option.toggleSelected(state) },
        enabled = isClickable,
        shape = MaterialTheme.shapes.medium,
        color = when {
            isSelected -> option.selectedColor.toComposeColor()
            else -> option.backgroundColor.toComposeColor()
        },
        modifier = modifier.padding(CardPadding)
    ) {
        CompositionLocalProvider(ToolTheme.LocalContentHorizontalPadding provides 8.dp) {
            Column(Modifier.padding(vertical = 8.dp)) {
                RenderContent(option.content, state)
            }
        }
    }
}

@Composable
private fun RenderMultiselectOptionCard(option: Multiselect.Option, state: State, modifier: Modifier = Modifier) {
    val isClickable by option.produceIsClickable(state)
    val isSelected by option.produceIsSelected(state)

    ElevatedCard(
        onClick = { option.toggleSelected(state) },
        enabled = isClickable,
        colors = CardDefaults.elevatedCardColors(
            containerColor = when {
                isSelected -> option.selectedColor.toComposeColor()
                else -> option.backgroundColor.toComposeColor()
            },
        ),
        elevation = ToolTheme.cardElevation(),
        modifier = modifier.padding(CardPadding)
    ) {
        Spacer(Modifier.height(8.dp))
        RenderContent(option.content, state)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun Multiselect.Option.produceIsClickable(state: State) =
    isClickableFlow(state).collectAsState(isClickable(state))

@Composable
private fun Multiselect.Option.produceIsSelected(state: State) = isSelectedFlow(state).collectAsState(isSelected(state))
