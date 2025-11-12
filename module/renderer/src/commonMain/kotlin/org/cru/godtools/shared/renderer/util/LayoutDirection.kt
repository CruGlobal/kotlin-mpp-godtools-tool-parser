package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import io.fluidsonic.locale.Locale

@Composable
internal fun ProvideLayoutDirectionFromLocale(locale: Locale?, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLayoutDirection provides (locale?.layoutDirection ?: LocalLayoutDirection.current),
        content = content,
    )
}

internal expect val Locale.layoutDirection: LayoutDirection
