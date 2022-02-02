package org.cru.godtools.tool.model

data class AspectRatio(val width: Int, val height: Int) {
    val ratio: Double get() = width.toDouble() / height

    internal companion object {
        internal fun String?.toAspectRatioOrNull() = this?.split(':')?.takeIf { it.size == 2 }?.let {
            try {
                AspectRatio(it[0].toInt(), it[1].toInt())
            } catch (_: NumberFormatException) {
                null
            }
        }
    }
}
