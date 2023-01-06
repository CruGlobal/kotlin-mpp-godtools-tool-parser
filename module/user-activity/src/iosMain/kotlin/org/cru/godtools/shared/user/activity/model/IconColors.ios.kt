package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGB

// TODO: Until material-color-utils has an ObjC/Kotlin implementation, we just store the generated values
internal actual fun IconColors(base: Color): IconColors = when (base.toSRGB()) {
    RGB("#05699B") -> IconColors(
        light = RGB("#006494"),
        dark = RGB("#8fcdff"),
        containerLight = RGB("#cbe6ff"),
        containerDark = RGB("#004b71"),
    )
    RGB("#2F3676") -> IconColors(
        light = RGB("#52599b"),
        dark = RGB("#bdc2ff"),
        containerLight = RGB("#dfe0ff"),
        containerDark = RGB("#3a4181"),
    )
    RGB("#A4D7C8") -> IconColors(
        light = RGB("#36675b"),
        dark = RGB("#9ed1c2"),
        containerLight = RGB("#b9edde"),
        containerDark = RGB("#1d4f44"),
    )
    RGB("#CEFFC1") -> IconColors(
        light = RGB("#3e6838"),
        dark = RGB("#a4d398"),
        containerLight = RGB("#bfefb3"),
        containerDark = RGB("#275023"),
    )
    RGB("#E0CE26") -> IconColors(
        light = RGB("#695f00"),
        dark = RGB("#dac91f"),
        containerLight = RGB("#f8e53f"),
        containerDark = RGB("#4f4800"),
    )
    RGB("#E55B36") -> IconColors(
        light = RGB("#ad3310"),
        dark = RGB("#ffb4a1"),
        containerLight = RGB("#ffdbd1"),
        containerDark = RGB("#881f00"),
    )
    else -> TODO("Unsupported color: ${base.toSRGB().toHex()}")
}
