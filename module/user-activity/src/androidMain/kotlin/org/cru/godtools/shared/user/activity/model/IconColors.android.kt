package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.RGBInt
import org.cru.godtools.shared.common.model.ThemeType
import palettes.TonalPalette

internal actual fun IconColors(base: Color): IconColors {
    val palette = TonalPalette.fromInt(RGBInt.convert(base).argb.toInt())
    return IconColors(
        light = RGBInt(palette.tone(40).toUInt()),
        containerLight = RGBInt(palette.tone(90).toUInt()),
        dark = RGBInt(palette.tone(80).toUInt()),
        containerDark = RGBInt(palette.tone(30).toUInt()),
    )
}

fun IconColors.color(mode: ThemeType) = _color(mode).toComposeColor()
fun IconColors.containerColor(mode: ThemeType) = _containerColor(mode).toComposeColor()
