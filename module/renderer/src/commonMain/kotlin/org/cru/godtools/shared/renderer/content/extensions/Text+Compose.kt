@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import org.cru.godtools.shared.tool.parser.model.Text

internal val Text.Align.textAlign get() = when (this) {
    Text.Align.START -> TextAlign.Start
    Text.Align.CENTER -> TextAlign.Center
    Text.Align.END -> TextAlign.End
}

internal val Text.Align.alignment get() = when (this) {
    Text.Align.START -> Alignment.Start
    Text.Align.CENTER -> Alignment.CenterHorizontally
    Text.Align.END -> Alignment.End
}
