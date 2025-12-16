package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.clickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.content.extensions.width
import org.cru.godtools.shared.renderer.extensions.toImageRequest
import org.cru.godtools.shared.renderer.extensions.toImageRequestBuilder
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Resource

internal const val TestTagImage = "image"

@Composable
internal fun ColumnScope.RenderImage(image: Image, state: State) = AsyncImage(
    model = image.resource?.toImageRequest(),
    contentDescription = null,
    contentScale = ContentScale.FillWidth,
    modifier = Modifier
        .testTag(TestTagImage)
        .visibility(image, state)
        .padding(horizontal = ContentHorizontalPadding)
        .width(image.width)
        .align(image.gravity.alignment)
        .clickable(image, state)
)

@Composable
internal fun RenderImageNode(resource: Resource, imageSize: Int, modifier: Modifier = Modifier) {
    val sizeResolver = rememberConstraintsSizeResolver()
    val painter = rememberAsyncImagePainter(
        model = resource.toImageRequestBuilder()
            .size(sizeResolver)
            .build()
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
                    !aspectRatio.isNaN() -> Modifier.aspectRatio(aspectRatio, aspectRatio < 1f)
                    else -> Modifier
                }
            ),
    )
}

private val Size.aspectRatio get() = width / height
