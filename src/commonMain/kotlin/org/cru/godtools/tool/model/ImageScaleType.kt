package org.cru.godtools.tool.model

private const val XML_FIT = "fit"
private const val XML_FILL = "fill"
private const val XML_FILL_X = "fill-x"
private const val XML_FILL_Y = "fill-y"

enum class ImageScaleType {
    FIT, FILL, FILL_X, FILL_Y;

    companion object {
        internal fun parseOrNull(value: String?) = when (value) {
            XML_FIT -> FIT
            XML_FILL -> FILL
            XML_FILL_X -> FILL_X
            XML_FILL_Y -> FILL_Y
            else -> null
        }
    }
}

internal fun String.toImageScaleTypeOrNull() = ImageScaleType.parseOrNull(this)