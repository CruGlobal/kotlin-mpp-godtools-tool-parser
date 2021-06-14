package org.cru.godtools.tool.model

import kotlin.native.concurrent.SharedImmutable
import kotlin.random.Random

@SharedImmutable
internal val TEST_GRAVITY = ImageGravity(Random.nextInt())
