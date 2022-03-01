package org.cru.godtools.tool.model

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
internal val TEST_GRAVITY = Gravity(Gravity.Horizontal.values().random(), Gravity.Vertical.values().random())

expect val TEST_URL: Uri
