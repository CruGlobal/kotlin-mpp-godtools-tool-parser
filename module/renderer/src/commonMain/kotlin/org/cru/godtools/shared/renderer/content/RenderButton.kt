package org.cru.godtools.shared.renderer.content

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import org.cru.godtools.shared.tool.parser.model.Button

@Composable
internal fun RenderButton(button: Button) {
    Button(
        onClick = { },
        colors = when (button.style) {
            Button.Style.OUTLINED -> ButtonDefaults.outlinedButtonColors()
            Button.Style.CONTAINED, Button.Style.UNKNOWN -> ButtonDefaults.buttonColors()
        },
        elevation = when (button.style) {
            Button.Style.OUTLINED -> null
            Button.Style.CONTAINED, Button.Style.UNKNOWN -> ButtonDefaults.buttonElevation()
        },
        border = when (button.style) {
            Button.Style.OUTLINED -> ButtonDefaults.outlinedButtonBorder()
            Button.Style.CONTAINED, Button.Style.UNKNOWN -> null
        }
    ) {
        RenderTextNode(button.text)
    }
}
