package org.cru.godtools.tool.model

import kotlin.random.Random

object TestColors {
    val RED = color(255, 0, 0, 1.0)
    val GREEN = color(0, 255, 0, 1.0)
    val BLUE = color(0, 0, 255, 1.0)
    val BLACK = color(0, 0, 0, 1.0)

    val RANDOM get() = color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256), Random.nextDouble())
}
