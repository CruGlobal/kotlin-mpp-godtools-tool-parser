package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.invisibleIf
import org.cru.godtools.shared.renderer.content.extensions.textAlign
import org.cru.godtools.shared.renderer.content.extensions.toPath
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.LocalResourceFileSystem
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun ColumnScope.RenderText(text: Text, state: State) = Row(
    modifier = Modifier
        .invisibleIf(content = text, state = state)
        .padding(horizontal = Horizontal_Padding)
        .fillMaxWidth()
) {
    val hasImage: Boolean = text.startImage != null || text.endImage != null
    val horizontalArrangement: Arrangement.Horizontal = if (hasImage) Arrangement.Center else Arrangement.Start

    RenderTextWithImages(
        text = text,
        state = state,
        horizontalArrangement = horizontalArrangement
    )
}

@Composable
private fun RowScope.RenderTextWithImages(text: Text, state: State, horizontalArrangement: Arrangement.Horizontal) = Row(
    modifier = Modifier
        .fillMaxWidth(),
    horizontalArrangement = horizontalArrangement
) {
    val imagePaddingToText: Int = 10

    text.startImage?.let {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .fileSystem(LocalResourceFileSystem.current)
                .data(it.toPath())
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(paddingValues = PaddingValues(end = imagePaddingToText.dp))
                .size(width = text.startImageSize.dp, height = text.startImageSize.dp)
        )
    }

    RenderTextNode(
        text = text,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .weight(1.0f, fill = false)
    )

    text.endImage?.let {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .fileSystem(LocalResourceFileSystem.current)
                .data(it.toPath())
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(paddingValues = PaddingValues(start = imagePaddingToText.dp))
                .size(width = text.endImageSize.dp, height = text.endImageSize.dp)
        )
    }
}

@Composable
internal fun RenderTextNode(text: Text, modifier: Modifier = Modifier) {
    Text(
        text.text,
        color = text.textColor.toComposeColor(),
        fontWeight = text.fontWeight?.let { FontWeight(it) },
        fontStyle = FontStyle.Italic.takeIf { Text.Style.ITALIC in text.textStyles },
        textDecoration = TextDecoration.Underline.takeIf { Text.Style.UNDERLINE in text.textStyles },
        textAlign = text.textAlign.textAlign,
        modifier = modifier
    )
}
