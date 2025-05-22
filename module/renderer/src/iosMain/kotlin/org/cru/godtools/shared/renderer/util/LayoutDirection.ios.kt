package org.cru.godtools.shared.renderer.util

import androidx.compose.ui.unit.LayoutDirection
import io.fluidsonic.locale.Locale

private val RTL_LANGUAGES = setOf(
    "ar", // Arabic
    "he", // Hebrew
    "ckb", // Sorani Kurdish
    "ps", // Pashto
    "fa", // Persian
    "ur", // Urdu
)

internal actual val Locale.layoutDirection: LayoutDirection
    get() = when (language) {
        in RTL_LANGUAGES -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
