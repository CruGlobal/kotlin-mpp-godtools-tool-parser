package org.cru.godtools.shared.tool.parser.util

import org.cru.godtools.shared.common.model.Uri

internal actual inline val Uri.scheme: String? get() = if (contains(":")) substringBefore(":") else null
