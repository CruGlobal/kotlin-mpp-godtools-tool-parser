package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.common.model.Uri

internal val TEST_GRAVITY = Gravity(Gravity.Horizontal.values().random(), Gravity.Vertical.values().random())

expect val TEST_URL: Uri
