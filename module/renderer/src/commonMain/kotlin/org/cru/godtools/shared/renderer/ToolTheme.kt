@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package org.cru.godtools.shared.renderer

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal object ToolTheme {
    internal val GodToolsGreen = Color(red = 0x6E, green = 0xDC, blue = 0x50)

    @get:Composable
    internal val ContentHorizontalPadding get() = LocalContentHorizontalPadding.current
    internal val LocalContentHorizontalPadding = compositionLocalOf { 16.dp }

    internal val ContentTextStyle = Typography().bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    internal val TractHeaderTextStyle = Typography().titleMedium.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp,
    )
    internal val TractHeroHeadingTextStyle = Typography().headlineMedium.copy(
        fontSize = 30.sp,
        lineHeight = 34.sp
    )

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
