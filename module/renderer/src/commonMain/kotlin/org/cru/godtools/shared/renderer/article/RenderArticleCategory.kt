package org.cru.godtools.shared.renderer.article

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.extensions.toImageRequest
import org.cru.godtools.shared.tool.parser.model.Category

@Composable
fun RenderArticleCategory(category: Category, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(15f / 8f),
    ) {
        AsyncImage(
            model = category.banner?.toImageRequest(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        category.label?.let {
            ProvideTextStyle(ToolTheme.ArticleLabelTextStyle) {
                RenderTextNode(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(20.dp),
                )
            }
        }
    }
}
