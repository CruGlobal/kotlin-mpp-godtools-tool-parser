package org.cru.godtools.shared.renderer.util

import android.text.TextUtils
import android.view.View
import androidx.compose.ui.unit.LayoutDirection
import io.fluidsonic.locale.Locale
import io.fluidsonic.locale.toPlatform

internal actual val Locale.layoutDirection: LayoutDirection
    get() = when (TextUtils.getLayoutDirectionFromLocale(toPlatform())) {
        View.LAYOUT_DIRECTION_RTL -> LayoutDirection.Rtl
        View.LAYOUT_DIRECTION_LTR -> LayoutDirection.Ltr
        else -> LayoutDirection.Ltr
    }
