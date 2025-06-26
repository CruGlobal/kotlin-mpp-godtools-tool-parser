package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.textAlign
import org.cru.godtools.shared.renderer.content.extensions.toPath
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.LocalResourceFileSystem
import org.cru.godtools.shared.tool.parser.model.Resource
import org.cru.godtools.shared.tool.parser.model.Text

private val DEFAULT_MIN_LINES = 1
private val IMAGE_PADDING_TO_TEXT = 8.dp

@Composable
internal fun ColumnScope.RenderText(text: Text, state: State) = Row(
    modifier = Modifier
        .visibility(text, state)
        .padding(horizontal = Horizontal_Padding)
        .fillMaxWidth()
) {
    text.startImage?.let {
        RenderImageNode(
            resource = it,
            imageSize = text.startImageSize,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = IMAGE_PADDING_TO_TEXT)
        )
    }

    RenderTextNode(
        text = text,
        modifier = Modifier
            .align(Alignment.Top)
            .weight(1.0f, fill = true)
    )

    text.endImage?.let {
        RenderImageNode(
            resource = it,
            imageSize = text.endImageSize,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = IMAGE_PADDING_TO_TEXT)
        )
    }
}

@Composable
internal fun RenderTextNode(text: Text, modifier: Modifier = Modifier) {
    Text(
        text.text,
        color = text.textColor.toComposeColor(),
        fontSize = TextUnit(LocalTextStyle.current.fontSize.value * text.textScale.toFloat(), LocalTextStyle.current.fontSize.type),
        fontWeight = text.fontWeight?.let { FontWeight(it) },
        fontStyle = FontStyle.Italic.takeIf { Text.Style.ITALIC in text.textStyles },
        textDecoration = TextDecoration.Underline.takeIf { Text.Style.UNDERLINE in text.textStyles },
        textAlign = text.textAlign.textAlign,
        minLines = if (text.minimumLines > DEFAULT_MIN_LINES) text.minimumLines else DEFAULT_MIN_LINES,
        modifier = modifier
    )
}

@Composable
private fun RenderImageNode(resource: Resource, imageSize: Int, modifier: Modifier = Modifier) {
    val sizeResolver = rememberConstraintsSizeResolver()
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .fileSystem(LocalResourceFileSystem.current)
            .data(resource.toPath())
            .size(sizeResolver)
            .build(),
    )
    val aspectRatio = painter.intrinsicSize.aspectRatio

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .sizeIn(maxWidth = imageSize.dp, maxHeight = imageSize.dp)
            .then(sizeResolver)
            .then(
                when {
                    !aspectRatio.isNaN() ->
                        Modifier.aspectRatio(aspectRatio, matchHeightConstraintsFirst = aspectRatio < 1f)
                    else -> Modifier
                },
            ),
    )
}

private val Size.aspectRatio get() = width / height
