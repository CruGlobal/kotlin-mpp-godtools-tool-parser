package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.handleClickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.content.extensions.width
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Gravity

private val ButtonVerticalPadding = 4.dp
private val ButtonIconPadding = 8.dp

@Composable
internal fun ColumnScope.RenderButton(button: Button, state: State) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val invisible by remember(button, state) {
        button.isInvisibleFlow(state)
    }.collectAsState(button.isInvisible(state))

    val minSize = LocalMinimumInteractiveComponentSize.current
    CompositionLocalProvider(
        // We decrease the minimum size to account for the additional vertical padding
        // we are adding before the minimum size modifier
        LocalMinimumInteractiveComponentSize provides minSize - (ButtonVerticalPadding * 2)
    ) {
        Button(
            onClick = { button.handleClickable(state, focusManager = focusManager, scope = scope) },
            enabled = !invisible,
            colors = when (button.style) {
                Button.Style.OUTLINED -> ButtonDefaults.outlinedButtonColors(
                    containerColor = button.backgroundColor.toComposeColor()
                )
                Button.Style.CONTAINED, Button.Style.UNKNOWN -> ButtonDefaults.buttonColors(
                    containerColor = button.buttonColor.toComposeColor()
                )
            },
            elevation = when (button.style) {
                Button.Style.OUTLINED -> null
                Button.Style.CONTAINED, Button.Style.UNKNOWN -> ButtonDefaults.buttonElevation()
            },
            border = when (button.style) {
                Button.Style.OUTLINED -> BorderStroke(1.dp, button.buttonColor.toComposeColor())
                Button.Style.CONTAINED, Button.Style.UNKNOWN -> null
            },
            modifier = Modifier
                .visibility(button, state)
                .padding(horizontal = ContentHorizontalPadding, vertical = ButtonVerticalPadding)
                .width(button.width)
                .align(button.gravity.alignment)
        ) {
            val icon = button.icon
            val iconGravity = button.iconGravity
            if (icon != null && iconGravity == Gravity.Horizontal.START) {
                RenderImageNode(icon, button.iconSize, modifier = Modifier.padding(end = ButtonIconPadding))
            }
            RenderTextNode(button.text)
            if (icon != null && iconGravity == Gravity.Horizontal.END) {
                RenderImageNode(icon, button.iconSize, modifier = Modifier.padding(start = ButtonIconPadding))
            }
        }
    }
}
