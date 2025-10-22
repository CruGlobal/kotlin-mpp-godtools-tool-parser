@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package org.cru.godtools.shared.renderer

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object ToolTheme {
    internal val GodToolsGreen = Color(red = 0x6E, green = 0xDC, blue = 0x50)

    @get:Composable
    internal val ContentHorizontalPadding get() = LocalContentHorizontalPadding.current
    internal val LocalContentHorizontalPadding = compositionLocalOf { 16.dp }

    @Composable
    fun cardElevation() = CardDefaults.elevatedCardElevation(
        defaultElevation = 3.dp,
        focusedElevation = 3.dp,
        hoveredElevation = 4.dp,
        pressedElevation = 1.dp,
        disabledElevation = 3.dp,
    )
    internal val CardPadding = 8.dp

    internal val ProgressBarHeight = 8.dp
    internal val ProgressBarGapSize = 0.dp - ProgressBarHeight
}
