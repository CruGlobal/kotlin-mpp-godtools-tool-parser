package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.handleClickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.content.extensions.width
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Button

@Composable
internal fun ColumnScope.RenderButton(button: Button, state: State) {
    val scope = rememberCoroutineScope()

    val invisible by remember(button, state) {
        button.isInvisibleFlow(state)
    }.collectAsState(button.isInvisible(state))

    Button(
        onClick = { button.handleClickable(state, scope) },
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
            .padding(horizontal = Horizontal_Padding)
            .width(button.width)
            .align(button.gravity.alignment)
    ) {
        RenderTextNode(button.text)
    }
}
