package org.cru.godtools.shared.tool.parser.internal

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGB

internal fun color(red: Int, green: Int, blue: Int, alpha: Double): Color =
    RGB(red / 255f, green / 255f, blue / 255f, alpha)
