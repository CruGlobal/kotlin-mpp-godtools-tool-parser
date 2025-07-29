package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.Color

data class Background(
    val color: Color,
    val image: Resource?,
    val imageGravity: Gravity,
    val imageScaleType: ImageScaleType,
)
