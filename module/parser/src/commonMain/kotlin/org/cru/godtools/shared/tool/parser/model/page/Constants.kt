package org.cru.godtools.shared.tool.parser.model.page

import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.model.color
import kotlin.native.concurrent.SharedImmutable

internal const val XMLNS_PAGE = "https://mobile-content-api.cru.org/xmlns/page"
internal const val XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance"

internal const val XML_CONTROL_COLOR = "control-color"

@AndroidColorInt
@SharedImmutable
internal val DEFAULT_CONTROL_COLOR = color(225, 225, 225, 1.0)
