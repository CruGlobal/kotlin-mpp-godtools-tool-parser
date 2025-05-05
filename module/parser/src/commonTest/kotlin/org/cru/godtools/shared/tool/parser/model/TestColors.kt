package org.cru.godtools.shared.tool.parser.model

import kotlin.random.Random
import org.cru.godtools.shared.tool.parser.internal.color

object TestColors {
    val RED = color(255, 0, 0, 1.0).toPlatformColor()
    val GREEN = color(0, 255, 0, 1.0).toPlatformColor()
    val BLUE = color(0, 0, 255, 1.0).toPlatformColor()
    val BLACK = color(0, 0, 0, 1.0).toPlatformColor()

    fun random() = color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256), Random.nextDouble())
        .toPlatformColor()
}
