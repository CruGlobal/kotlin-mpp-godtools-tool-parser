package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import coil3.compose.AsyncImage
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.clickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.content.extensions.width
import org.cru.godtools.shared.renderer.extensions.toImageRequest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Image

internal const val TestTagImage = "image"

@Composable
internal fun ColumnScope.RenderImage(image: Image, state: State) {
    val scope = rememberCoroutineScope()

    AsyncImage(
        model = image.resource?.toImageRequest(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .testTag(TestTagImage)
            .visibility(image, state)
            .padding(horizontal = HorizontalPadding)
            .width(image.width)
            .align(image.gravity.alignment)
            .clickable(image, state, scope)
    )
}
