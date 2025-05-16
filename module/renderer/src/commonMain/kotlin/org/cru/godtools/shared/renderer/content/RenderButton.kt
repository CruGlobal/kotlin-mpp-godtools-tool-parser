package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.tool.parser.model.Button

@Composable
internal fun RenderButton(button: Button) {
    Button(
        onClick = { },
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
        }
    ) {
        RenderTextNode(button.text)
    }
}
