package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import org.cru.godtools.shared.renderer.content.extensions.textAlign
import org.cru.godtools.shared.renderer.content.extensions.toPath
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.LocalResourceFileSystem
import org.cru.godtools.shared.tool.parser.model.Resource
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun ColumnScope.RenderText(text: Text, state: State) = Row(
    modifier = Modifier
        .visibility(text, state)
        .padding(horizontal = Horizontal_Padding)
        .fillMaxWidth()
) {
    val imagePaddingToText: Int = 10

    text.startImage?.let {
        RenderImageNode(
            resource = it,
            imageSize = text.startImageSize,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(paddingValues = PaddingValues(end = imagePaddingToText.dp))
        )
    }

    RenderTextNode(
        text = text,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .weight(1.0f, fill = true)
    )

    text.endImage?.let {
        RenderImageNode(
            resource = it,
            imageSize = text.endImageSize,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(paddingValues = PaddingValues(start = imagePaddingToText.dp))
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

@Composable
private fun RenderImageNode(resource: Resource, imageSize: Int, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .fileSystem(LocalResourceFileSystem.current)
            .data(resource.toPath())
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(width = imageSize.dp, height = imageSize.dp)
    )
}
