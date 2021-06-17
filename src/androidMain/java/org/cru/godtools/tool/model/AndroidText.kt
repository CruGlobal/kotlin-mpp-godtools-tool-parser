package org.cru.godtools.tool.model

import android.graphics.Typeface
import android.view.Gravity
import splitties.bitflags.withFlag

val Text.typefaceStyle
    get() = Typeface.NORMAL
        .withFlag(if (Text.Style.BOLD in textStyles) Typeface.BOLD else 0)
        .withFlag(if (Text.Style.ITALIC in textStyles) Typeface.ITALIC else 0)

val Text.Align.gravity get() = when (this) {
    Text.Align.START -> Gravity.START
    Text.Align.CENTER -> Gravity.CENTER_HORIZONTAL
    Text.Align.END -> Gravity.END
}
