package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidColorInt

interface Styles : Base {
    @get:AndroidColorInt
    val primaryColor: Color get() = stylesParent.primaryColor
    @get:AndroidColorInt
    val primaryTextColor: Color get() = stylesParent.primaryTextColor
}

@get:AndroidColorInt
val Styles?.primaryColor get() = this?.primaryColor ?: Manifest.DEFAULT_PRIMARY_COLOR
@get:AndroidColorInt
val Styles?.primaryTextColor get() = this?.primaryTextColor ?: Manifest.DEFAULT_PRIMARY_TEXT_COLOR
