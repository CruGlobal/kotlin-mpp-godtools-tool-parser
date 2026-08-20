package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.ic_call_to_action
import org.cru.godtools.shared.renderer.generated.resources.tract_accessibility_action_next_page
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.controlColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RenderTractCallToAction(page: TractPage, onNextPage: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        ProvideTextStyle(ToolTheme.ContentTextStyle) {
            when (val label = page.callToAction.label) {
                null -> Spacer(Modifier.weight(1f))

                else -> RenderTextNode(
                    label,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 32.dp)
                )
            }
        }
        if (!page.isLastPage) {
            IconButton(
                onClick = onNextPage,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = page.callToAction.controlColor.toComposeColor(),
                ),
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Icon(
                    painterResource(Res.drawable.ic_call_to_action),
                    contentDescription = stringResource(Res.string.tract_accessibility_action_next_page),
                )
            }
        }
    }
}
