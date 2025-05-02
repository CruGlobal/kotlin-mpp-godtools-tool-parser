package org.cru.godtools.shared.renderer.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun RenderText(text: Text, modifier: Modifier = Modifier) {
    Text(
        text.text,
        modifier = modifier
    )
}
