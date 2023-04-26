@file:JvmMultifileClass
@file:JvmName("TextKt")

package org.cru.godtools.shared.tool.parser.model

import android.view.Gravity
import androidx.annotation.ColorInt

val Text.Align.gravity get() = when (this) {
    Text.Align.START -> Gravity.START
    Text.Align.CENTER -> Gravity.CENTER_HORIZONTAL
    Text.Align.END -> Gravity.END
}

val Text?.textAlign get() = this?.textAlign ?: stylesParent.textAlign
@get:ColorInt
val Text?.textColor get() = this?.textColor ?: stylesParent.textColor
val Text?.textScale get() = this?.textScale ?: stylesParent.textScale
