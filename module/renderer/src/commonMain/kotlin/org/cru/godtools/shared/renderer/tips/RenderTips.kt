package org.cru.godtools.shared.renderer.tips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import org.cru.godtools.shared.renderer.content.extensions.painterTip
import org.cru.godtools.shared.renderer.content.extensions.tipBackground
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.tips.Tip

internal val TipSize = 40.dp
internal val TipIconSize = 24.dp
internal val TipElevation = 4.dp

internal val TipArrowHeight = 48.dp
private val TipArrowSize = 12.dp
internal val TipCornerSize = CornerSize(6.dp)

@Composable
internal fun TipUpArrow(tip: Tip, state: State, modifier: Modifier = Modifier) {
    val isComplete by tip.produceIsComplete()

    Surface(
        onClick = { state.triggerEvent(State.Event.OpenTip(tip.id)) },
        shape = TipUpArrowShape,
        shadowElevation = TipElevation,
        modifier = modifier
            .requiredSize(width = TipSize, height = TipArrowHeight),
    ) {
        Image(
            painterTip(tip, isComplete = isComplete),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .tipBackground(isComplete = isComplete)
                .padding(top = TipArrowHeight - TipSize)
                .wrapContentSize()
                .size(TipIconSize)
        )
    }
}

private object TipUpArrowShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val width = size.width
        val height = size.height
        val arrowHeight = with(density) { TipArrowSize.toPx() }
        val roundedCorner = CornerRounding(TipCornerSize.toPx(size, density), 1f)

        val polygon = RoundedPolygon(
            vertices = floatArrayOf(
                0f, height, // bottom left
                width, height, // bottom right
                width, arrowHeight, // top right
                width / 2, 0f, // point
                0f, arrowHeight, // top left
            ),
            perVertexRounding = listOf(
                roundedCorner,
                roundedCorner,
                roundedCorner,
                CornerRounding.Unrounded,
                roundedCorner,
            ),
            centerX = width / 2,
            centerY = height / 2,
        )

        return Outline.Generic(polygon.toComposePath())
    }
}

// TODO: Switch to official extension method when available
private fun RoundedPolygon.toComposePath(scale: Float = 1f, path: Path = Path()): Path {
    var first = true
    path.rewind()
    cubics.forEach { bezier ->
        if (first) {
            path.moveTo(bezier.anchor0X * scale, bezier.anchor0Y * scale)
            first = false
        }
        path.cubicTo(
            bezier.control0X * scale,
            bezier.control0Y * scale,
            bezier.control1X * scale,
            bezier.control1Y * scale,
            bezier.anchor1X * scale,
            bezier.anchor1Y * scale,
        )
    }
    path.close()
    return path
}
