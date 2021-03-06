package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE

interface Styles : Base {
    companion object {
        internal const val DEFAULT_TEXT_SCALE = 1.0
    }

    @get:AndroidColorInt
    val primaryColor: PlatformColor get() = stylesParent.primaryColor
    @get:AndroidColorInt
    val primaryTextColor: PlatformColor get() = stylesParent.primaryTextColor

    // region Button styles
    val buttonStyle: Button.Style get() = stylesParent.buttonStyle
    @get:AndroidColorInt
    val buttonColor: PlatformColor? get() = stylesParent?.buttonColor
    // endregion Button styles

    // region Multiselect styles
    @get:AndroidColorInt
    val multiselectOptionBackgroundColor: PlatformColor get() = stylesParent.multiselectOptionBackgroundColor
    @get:AndroidColorInt
    val multiselectOptionSelectedColor: PlatformColor? get() = stylesParent?.multiselectOptionSelectedColor
    // endregion Multiselect styles

    // region Text styles
    @get:AndroidColorInt
    val textColor: PlatformColor get() = stylesParent.textColor
    val textScale: Double get() = stylesParent.textScale
    val textAlign: Text.Align get() = stylesParent.textAlign
    // endregion Text styles
}

@get:AndroidColorInt
val Styles?.primaryColor get() = this?.primaryColor ?: Manifest.DEFAULT_PRIMARY_COLOR
@get:AndroidColorInt
val Styles?.primaryTextColor get() = this?.primaryTextColor ?: Manifest.DEFAULT_PRIMARY_TEXT_COLOR

val Styles?.buttonStyle get() = this?.buttonStyle ?: Manifest.DEFAULT_BUTTON_STYLE

// region Multiselect styles
internal val Styles?.multiselectOptionBackgroundColor
    get() = this?.multiselectOptionBackgroundColor ?: manifest.backgroundColor
// endregion Multiselect styles

// region Text styles
@get:AndroidColorInt
val Styles?.textColor get() = this?.textColor ?: Manifest.DEFAULT_TEXT_COLOR
val Styles?.textScale get() = this?.textScale ?: DEFAULT_TEXT_SCALE
val Styles?.textAlign get() = this?.textAlign ?: Manifest.DEFAULT_TEXT_ALIGN
// endregion Text styles
