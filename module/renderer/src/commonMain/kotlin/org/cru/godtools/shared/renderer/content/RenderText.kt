package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import org.cru.godtools.shared.tool.parser.model.Resource
import org.cru.godtools.shared.tool.parser.model.Text

@Composable
internal fun ColumnScope.RenderText(text: Text, state: State) = Row(
    modifier = Modifier
        .invisibleIf(content = text, state = state)
        .padding(horizontal = Horizontal_Padding)
        .fillMaxWidth()
) {
    RenderTextWithImages(
        text = text,
        state = state
    )
}

@Composable
private fun RowScope.RenderTextWithImages(
    text: Text,
    state: State
) = Row(modifier = Modifier.fillMaxWidth()) {

    text.startImage?.let {
        RenderTextImage(
            resource = it,
            imageSize = text.startImageSize
        )

        //Spacer(modifier = Modifier.weight(1.0f))
    }

    RenderTextNode(
        text = text,
        modifier = Modifier
            .align(Alignment.CenterVertically)
    )

    text.endImage?.let {

        //Spacer(modifier = Modifier.weight(1.0f))

        RenderTextImage(
            resource = it,
            imageSize = text.endImageSize
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
private fun RenderTextImage(resource: Resource, imageSize: Int) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .fileSystem(LocalResourceFileSystem.current)
            .data(resource.toPath())
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .size(width = imageSize.dp, height = imageSize.dp)
    )
}

