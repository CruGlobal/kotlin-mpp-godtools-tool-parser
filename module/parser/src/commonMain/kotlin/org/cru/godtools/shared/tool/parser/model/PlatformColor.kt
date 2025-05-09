package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGB

expect class PlatformColor

internal expect fun PlatformColor.toRGB(): RGB
internal expect fun Color.toPlatformColor(): PlatformColor
internal fun PlatformColor.toHSL() = toRGB().toHSL()
