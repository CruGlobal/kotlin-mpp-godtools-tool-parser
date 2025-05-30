@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign

internal val TextAlign.alignment get() = when (this) {
    TextAlign.Start -> Alignment.CenterStart
    TextAlign.Center -> Alignment.Center
    TextAlign.End -> Alignment.CenterEnd
    else -> Alignment.CenterStart
}
