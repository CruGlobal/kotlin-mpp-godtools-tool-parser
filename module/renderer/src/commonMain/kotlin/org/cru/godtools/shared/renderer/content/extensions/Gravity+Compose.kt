@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import org.cru.godtools.shared.tool.parser.model.Gravity

internal val Gravity.Horizontal.alignment get() = when (this) {
    Gravity.Horizontal.START -> Alignment.Start
    Gravity.Horizontal.CENTER -> Alignment.CenterHorizontally
    Gravity.Horizontal.END -> Alignment.End
}

internal val Gravity.Horizontal.arrangement get() = when (this) {
    Gravity.Horizontal.START -> Arrangement.Start
    Gravity.Horizontal.CENTER -> Arrangement.Center
    Gravity.Horizontal.END -> Arrangement.End
}
