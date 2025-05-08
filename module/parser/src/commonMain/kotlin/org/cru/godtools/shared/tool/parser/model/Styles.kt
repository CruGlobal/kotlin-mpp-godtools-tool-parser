package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.Color
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_ALIGN
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE

interface Styles : Base {
    companion object {
        internal val DEFAULT_TEXT_ALIGN = Text.Align.START
        internal const val DEFAULT_TEXT_SCALE = 1.0
    }

    @get:AndroidColorInt
    val primaryColor: PlatformColor get() = stylesParent.primaryColor
    val primaryTextColor: Color get() = stylesParent.primaryTextColor

    // region Button styles
    val buttonStyle: Button.Style get() = stylesParent.buttonStyle
    val buttonColor: Color? get() = stylesParent?.buttonColor
    // endregion Button styles

    // region Card styles
    val cardBackgroundColor: PlatformColor get() = stylesParent.cardBackgroundColor
    // endregion Card styles

    // region Multiselect styles
    @get:AndroidColorInt
    val multiselectOptionBackgroundColor: PlatformColor get() = stylesParent.multiselectOptionBackgroundColor
    @get:AndroidColorInt
    val multiselectOptionSelectedColor: PlatformColor? get() = stylesParent?.multiselectOptionSelectedColor
    // endregion Multiselect styles

    // region Text styles
    val textColor: Color get() = stylesParent.textColor
    val textScale: Double get() = stylesParent.textScale
    val textAlign: Text.Align get() = stylesParent.textAlign
    // endregion Text styles
}

@get:AndroidColorInt
val Styles?.primaryColor get() = this?.primaryColor ?: Manifest.DEFAULT_PRIMARY_COLOR
val Styles?.primaryTextColor get() = this?.primaryTextColor ?: Manifest.DEFAULT_PRIMARY_TEXT_COLOR

val Styles?.buttonStyle get() = this?.buttonStyle ?: Manifest.DEFAULT_BUTTON_STYLE

// region Card styles
internal val Styles?.cardBackgroundColor get() = this?.cardBackgroundColor ?: Manifest.DEFAULT_BACKGROUND_COLOR
// endregion Card styles

// region Multiselect styles
internal val Styles?.multiselectOptionBackgroundColor
    get() = this?.multiselectOptionBackgroundColor ?: this?.manifest.backgroundColor
// endregion Multiselect styles

// region Text styles
val Styles?.textAlign get() = this?.textAlign ?: DEFAULT_TEXT_ALIGN
val Styles?.textColor get() = this?.textColor ?: Manifest.DEFAULT_TEXT_COLOR
val Styles?.textScale get() = this?.textScale ?: DEFAULT_TEXT_SCALE
// endregion Text styles
