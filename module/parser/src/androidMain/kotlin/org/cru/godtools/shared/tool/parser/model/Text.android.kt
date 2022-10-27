package org.cru.godtools.shared.tool.parser.model

import android.view.Gravity

val Text.Align.gravity get() = when (this) {
    Text.Align.START -> Gravity.START
    Text.Align.CENTER -> Gravity.CENTER_HORIZONTAL
    Text.Align.END -> Gravity.END
}
