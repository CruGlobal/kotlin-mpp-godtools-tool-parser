@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.tool.parser.model.Dimension

internal fun Modifier.width(width: Dimension) = when (width) {
    is Dimension.Percent -> fillMaxWidth(width.value)
    is Dimension.Pixels -> width(width.value.dp)
}
