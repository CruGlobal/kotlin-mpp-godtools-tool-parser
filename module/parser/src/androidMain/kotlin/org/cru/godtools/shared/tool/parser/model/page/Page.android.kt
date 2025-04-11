@file:JvmMultifileClass
@file:JvmName("PageKt")

package org.cru.godtools.shared.tool.parser.model.page

import androidx.annotation.ColorInt

@get:ColorInt
val Page?.controlColor get() = this?.controlColor ?: DEFAULT_CONTROL_COLOR
