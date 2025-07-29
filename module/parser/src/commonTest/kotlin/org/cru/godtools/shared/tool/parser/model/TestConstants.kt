package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.common.model.Uri

internal val TEST_GRAVITY = Gravity(Gravity.Horizontal.entries.random(), Gravity.Vertical.entries.random())

expect val TEST_URL: Uri
