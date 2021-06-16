package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE

private class StylesOverride(
    parent: Base,
    private val _textScale: Double
) : BaseModel(parent), Styles {
    override val textScale get() = super.textScale * _textScale
}

internal fun Base.stylesOverride(textScale: Double = DEFAULT_TEXT_SCALE): Base = StylesOverride(this, textScale)
