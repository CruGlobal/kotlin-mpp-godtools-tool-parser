package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE

private class StylesOverride(
    parent: Base,
    private val _textAlign: (() -> Text.Align?)?,
    private val _textColor: (() -> PlatformColor?)?,
    private val _textScale: Double
) : BaseModel(parent), Styles {
    override val textAlign get() = _textAlign?.invoke() ?: super.textAlign
    override val textColor get() = _textColor?.invoke() ?: super.textColor
    override val textScale get() = super.textScale * _textScale
}

internal fun Base.stylesOverride(
    textAlign: (() -> Text.Align?)? = null,
    textColor: (() -> PlatformColor?)? = null,
    textScale: Double = DEFAULT_TEXT_SCALE
): Base = StylesOverride(this, textAlign, textColor, textScale)
