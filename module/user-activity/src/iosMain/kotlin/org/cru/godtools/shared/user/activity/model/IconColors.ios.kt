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
    RGB("#292c67") -> IconColors(
        light = RGB("#555996"),
        dark = RGB("#bfc1ff"),
        containerLight = RGB("#e1e0ff"),
        containerDark = RGB("#3e417d"),
    )
    RGB("#2F3676") -> IconColors(
        light = RGB("#52599b"),
        dark = RGB("#bdc2ff"),
        containerLight = RGB("#dfe0ff"),
        containerDark = RGB("#3a4181"),
    )
    RGB("#62CCF3") -> IconColors(
        light = RGB("#006782"),
        dark = RGB("#6ad3fa"),
        containerLight = RGB("#baeaff"),
        containerDark = RGB("#004d62"),
    )
    RGB("#A43E95") -> IconColors(
        light = RGB("#9a358c"),
        dark = RGB("#ffaceb"),
        containerLight = RGB("#ffd7f1"),
        containerDark = RGB("#7d1972"),
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
    RGB("#d5d5d5") -> IconColors(
        light = RGB("#5d5e5f"),
        dark = RGB("#c6c6c6"),
        containerLight = RGB("#e2e2e2"),
        containerDark = RGB("#454747"),
    )
    RGB("#E0CE26") -> IconColors(
        light = RGB("#695f00"),
        dark = RGB("#dac91f"),
        containerLight = RGB("#f8e53f"),
        containerDark = RGB("#4f4800"),
    )
    RGB("#E53660") -> IconColors(
        light = RGB("#bb0d44"),
        dark = RGB("#ffb2bb"),
        containerLight = RGB("#ffd9dc"),
        containerDark = RGB("#910032"),
    )
    RGB("#E55B36") -> IconColors(
        light = RGB("#ad3310"),
        dark = RGB("#ffb4a1"),
        containerLight = RGB("#ffdbd1"),
        containerDark = RGB("#881f00"),
    )
    else -> TODO("Unsupported color: ${base.toSRGB().toHex()}")
}
