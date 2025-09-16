package org.cru.godtools.shared.renderer

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

internal object ToolTheme {
    internal val ContentHorizontalPadding = 16.dp

    @Composable
    fun cardElevation() = CardDefaults.elevatedCardElevation(
        defaultElevation = 3.dp,
        focusedElevation = 3.dp,
        hoveredElevation = 4.dp,
        pressedElevation = 1.dp,
        disabledElevation = 3.dp,
    )
}
