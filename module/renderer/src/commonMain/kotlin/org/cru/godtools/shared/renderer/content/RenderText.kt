package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.resolveDefaults
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.textAlign
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Text

private val DEFAULT_MIN_LINES = 1
private val IMAGE_PADDING_TO_TEXT = 8.dp

internal const val TestTagTextStartImage = "text-start-image"
internal const val TestTagTextEndImage = "text-end-image"

private const val LayoutIdStartImage = "start-image"
private const val LayoutIdText = "text"
private const val LayoutIdEndImage = "end-image"

@Composable
internal fun ColumnScope.RenderText(text: Text, state: State) = Layout(
    content = {
        text.startImage?.let {
            RenderImageNode(
                resource = it,
                imageSize = text.startImageSize,
                modifier = Modifier
                    .layoutId(LayoutIdStartImage)
                    .testTag(TestTagTextStartImage)
            )
        }
        RenderTextNode(text, modifier = Modifier.layoutId(LayoutIdText))
        text.endImage?.let {
            RenderImageNode(
                resource = it,
                imageSize = text.endImageSize,
                modifier = Modifier
                    .layoutId(LayoutIdEndImage)
                    .testTag(TestTagTextEndImage)
            )
        }
    },
    modifier = Modifier
        .visibility(text, state)
        .padding(horizontal = ContentHorizontalPadding)
        .fillMaxWidth()
) { measurables, constraints ->
    val startImage = measurables.firstOrNull { it.layoutId == LayoutIdStartImage }?.measure(Constraints())
    val endImage = measurables.firstOrNull { it.layoutId == LayoutIdEndImage }?.measure(Constraints())

    // calculate the textWidth based upon the start & end image
    val startImageSpace = startImage?.let { it.width + IMAGE_PADDING_TO_TEXT.roundToPx() } ?: 0
    val endImageSpace = endImage?.let { it.width + IMAGE_PADDING_TO_TEXT.roundToPx() } ?: 0
    val width = constraints.maxWidth
    val textWidth = (width - startImageSpace - endImageSpace).coerceAtLeast(0)
    val textNode = measurables.first { it.layoutId == LayoutIdText }
        .measure(constraints.copy(minWidth = textWidth, maxWidth = textWidth, minHeight = 0))

    // Calculate Vertical positions relative to the top of the text: each image anchors to
    // the text bounds independently, so an image taller than the text ends up with a negative y
    // (extending above the text) and/or a bottom edge past the text height (extending below it).
    fun calculateImageY(align: Gravity.Vertical, imageHeight: Int) = when (align) {
        Gravity.Vertical.TOP -> 0
        Gravity.Vertical.CENTER -> (textNode.height - imageHeight) / 2
        Gravity.Vertical.BOTTOM -> textNode.height - imageHeight
    }
    val startImageY = startImage?.let { calculateImageY(text.startImageAlign, it.height) } ?: 0
    val endImageY = endImage?.let { calculateImageY(text.endImageAlign, it.height) } ?: 0

    // `offset` then shifts all children down so the topmost child sits at y=0, and the node height
    // reaches the bottommost child, making the node fully contain the text and any images.
    val offset = -minOf(0, startImageY, endImageY)
    val height = maxOf(
        textNode.height,
        startImageY + (startImage?.height ?: 0),
        endImageY + (endImage?.height ?: 0),
    ) + offset

    layout(width, height) {
        startImage?.placeRelative(0, startImageY + offset)
        textNode.placeRelative(startImageSpace, offset)
        endImage?.placeRelative(width - endImage.width, endImageY + offset)
    }
}

@Composable
internal fun RenderTextNode(text: Text, modifier: Modifier = Modifier) {
    val defaultTextStyle = resolveDefaults(LocalTextStyle.current, LocalLayoutDirection.current)

    Text(
        text.text,
        color = text.textColor.toComposeColor(),
        fontSize = defaultTextStyle.fontSize * text.textScale,
        fontWeight = text.fontWeight?.let { FontWeight(it) },
        fontStyle = FontStyle.Italic.takeIf { Text.Style.ITALIC in text.textStyles },
        textDecoration = TextDecoration.Underline.takeIf { Text.Style.UNDERLINE in text.textStyles },
        textAlign = text.textAlign.textAlign,
        lineHeight = defaultTextStyle.lineHeight.let { if (it.isSpecified) it * text.textScale else it },
        minLines = text.minimumLines.coerceAtLeast(DEFAULT_MIN_LINES),
        modifier = modifier,
    )
}
