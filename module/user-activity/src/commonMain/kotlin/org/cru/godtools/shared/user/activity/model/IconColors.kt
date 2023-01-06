package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.Color
import org.cru.godtools.shared.common.model.ThemeType
import org.cru.godtools.shared.common.model.toPlatformColor

data class IconColors internal constructor(
    private val light: Color,
    private val dark: Color,
    private val containerLight: Color,
    private val containerDark: Color
) {
    fun color(mode: ThemeType) = when (mode) {
        ThemeType.LIGHT -> light
        ThemeType.DARK -> dark
    }.toPlatformColor()

    fun containerColor(mode: ThemeType) = when (mode) {
        ThemeType.LIGHT -> containerLight
        ThemeType.DARK -> containerDark
    }.toPlatformColor()

    override fun toString() = "IconColors(" +
        "light=${light.toSRGB().toHex()}, " +
        "dark=${dark.toSRGB().toHex()}, " +
        "containerLight=${containerLight.toSRGB().toHex()}, " +
        "containerDark=${containerDark.toSRGB().toHex()}" +
        ")"
}

// TODO: Utilize material-color-utils for this if it ever has a kotlin multiplatform version
internal expect fun IconColors(base: Color): IconColors
