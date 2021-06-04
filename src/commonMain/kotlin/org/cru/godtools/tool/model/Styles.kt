package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidColorInt

interface Styles : Base {
    @get:AndroidColorInt
    val primaryColor: Color get() = stylesParent.primaryColor
    @get:AndroidColorInt
    val primaryTextColor: Color get() = stylesParent.primaryTextColor

    // region Button styles
    val buttonStyle: Button.Style get() = stylesParent.buttonStyle
    @get:AndroidColorInt
    val buttonColor: Color? get() = stylesParent?.buttonColor
    // endregion Button styles

    // region Text styles
    @get:AndroidColorInt
    val textColor: Color get() = stylesParent.textColor
    val textScale: Double get() = stylesParent.textScale
    val textAlign: Text.Align get() = stylesParent.textAlign
    // endregion Text styles
}

@get:AndroidColorInt
val Styles?.primaryColor get() = this?.primaryColor ?: Manifest.DEFAULT_PRIMARY_COLOR
@get:AndroidColorInt
val Styles?.primaryTextColor get() = this?.primaryTextColor ?: Manifest.DEFAULT_PRIMARY_TEXT_COLOR

val Styles?.buttonStyle get() = this?.buttonStyle ?: Manifest.DEFAULT_BUTTON_STYLE

// region Text styles
@get:AndroidColorInt
val Styles?.textColor get() = this?.textColor ?: Manifest.DEFAULT_TEXT_COLOR
val Styles?.textScale get() = this?.textScale ?: Manifest.DEFAULT_TEXT_SCALE
val Styles?.textAlign get() = this?.textAlign ?: Manifest.DEFAULT_TEXT_ALIGN
// endregion Text styles
