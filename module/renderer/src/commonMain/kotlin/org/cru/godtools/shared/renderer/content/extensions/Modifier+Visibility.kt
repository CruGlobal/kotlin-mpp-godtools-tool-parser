@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent

fun Modifier.invisibleIf(invisible: Boolean) = if (invisible) drawWithContent { } else this

inline fun Modifier.invisibleIf(crossinline invisible: () -> Boolean) =
    drawWithContent { if (!invisible()) drawContent() }
