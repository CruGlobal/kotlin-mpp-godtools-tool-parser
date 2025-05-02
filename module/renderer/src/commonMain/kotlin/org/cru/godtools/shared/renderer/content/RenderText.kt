package org.cru.godtools.shared.renderer.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun RenderText(text: Text) {
    Text(
        text.text,
        fontWeight = text.fontWeight?.let { FontWeight(it) },
        fontStyle = FontStyle.Italic.takeIf { Text.Style.ITALIC in text.textStyles },
        textDecoration = TextDecoration.Underline.takeIf { Text.Style.UNDERLINE in text.textStyles },
    )
}
