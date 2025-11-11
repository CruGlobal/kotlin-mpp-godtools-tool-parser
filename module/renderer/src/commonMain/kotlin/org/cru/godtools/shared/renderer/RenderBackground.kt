package org.cru.godtools.shared.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.extensions.toImageRequest
import org.cru.godtools.shared.tool.parser.model.Background
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.ImageScaleType

@Composable
internal fun RenderBackground(bkg: Background, modifier: Modifier = Modifier) {
    val image = bkg.image
    if (image != null) {
        val horizontalAlignment = when (bkg.imageGravity.horizontal) {
            Gravity.Horizontal.START -> Alignment.Start
            Gravity.Horizontal.CENTER -> Alignment.CenterHorizontally
            Gravity.Horizontal.END -> Alignment.End
        }
        val verticalAlignment = when (bkg.imageGravity.vertical) {
            Gravity.Vertical.TOP -> Alignment.Top
            Gravity.Vertical.CENTER -> Alignment.CenterVertically
            Gravity.Vertical.BOTTOM -> Alignment.Bottom
        }
        AsyncImage(
            model = image.toImageRequest(),
            contentDescription = null,
            alignment = horizontalAlignment + verticalAlignment,
            contentScale = when (bkg.imageScaleType) {
                ImageScaleType.FIT -> ContentScale.Fit
                ImageScaleType.FILL -> ContentScale.Crop
                ImageScaleType.FILL_X -> ContentScale.FillWidth
                ImageScaleType.FILL_Y -> ContentScale.FillHeight
            },
            modifier = modifier.background(bkg.color.toComposeColor())
        )
    } else {
        Box(modifier.background(bkg.color.toComposeColor()))
    }
}
