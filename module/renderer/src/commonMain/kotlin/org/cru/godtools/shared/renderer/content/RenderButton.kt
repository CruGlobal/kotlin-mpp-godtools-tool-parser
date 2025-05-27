package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.handleClickable
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Dimension

@Composable
internal fun ColumnScope.RenderButton(button: Button, state: State) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = { button.handleClickable(state, scope) },
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
            .padding(horizontal = Horizontal_Padding)
            .align(button.gravity.alignment)
            .then(
                when (val width = button.width) {
                    is Dimension.Percent -> Modifier.fillMaxWidth(width.value)
                    is Dimension.Pixels -> Modifier.width(width.value.dp)
                }
            )
    ) {
        RenderTextNode(button.text)
    }
}
