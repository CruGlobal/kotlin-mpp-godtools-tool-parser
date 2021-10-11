package org.cru.godtools.tool.model.lesson

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.model.color
import kotlin.native.concurrent.SharedImmutable

internal const val XMLNS_LESSON = "https://mobile-content-api.cru.org/xmlns/lesson"

@AndroidColorInt
@SharedImmutable
internal val DEFAULT_LESSON_NAV_BAR_COLOR = color(0, 0, 0, 0.0)
