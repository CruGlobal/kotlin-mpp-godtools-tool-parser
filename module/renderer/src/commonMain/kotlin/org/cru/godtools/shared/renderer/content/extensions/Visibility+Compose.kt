@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Content

@Composable
fun Modifier.invisibleIf(content: Content, state: State): Modifier {
    val invisible by remember(content, state) { content.isInvisibleFlow(state) }.collectAsState(false)

    return if (invisible) drawWithContent { } else this
}
