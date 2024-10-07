package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.Color
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.common.model.ThemeType

data class IconColors internal constructor(
    @VisibleForTesting
    internal val light: Color,
    @VisibleForTesting
    internal val dark: Color,
    @VisibleForTesting
    internal val containerLight: Color,
    @VisibleForTesting
    internal val containerDark: Color,
) {
    @Suppress("FunctionName")
    internal fun _color(mode: ThemeType) = when (mode) {
        ThemeType.LIGHT -> light
        ThemeType.DARK -> dark
    }

    @Suppress("FunctionName")
    internal fun _containerColor(mode: ThemeType) = when (mode) {
        ThemeType.LIGHT -> containerLight
        ThemeType.DARK -> containerDark
    }

    internal fun alpha(alpha: Float) = IconColors(
        light = light.toSRGB().copy(alpha = alpha),
        dark = dark.toSRGB().copy(alpha = alpha),
        containerLight = containerLight.toSRGB().copy(alpha = alpha),
        containerDark = containerDark.toSRGB().copy(alpha = alpha),
    )

    override fun toString() = "IconColors(" +
        "light=${light.toSRGB().toHex()}, " +
        "dark=${dark.toSRGB().toHex()}, " +
        "containerLight=${containerLight.toSRGB().toHex()}, " +
        "containerDark=${containerDark.toSRGB().toHex()}" +
        ")"
}

// TODO: Utilize material-color-utils for this if it ever has a kotlin multiplatform version
internal expect fun IconColors(base: Color): IconColors
