package org.cru.godtools.shared.renderer.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun RenderText(text: Text) {
    Text(
        text.text,
    )
}
