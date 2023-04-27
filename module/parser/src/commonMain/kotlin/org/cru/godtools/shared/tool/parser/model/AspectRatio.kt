package org.cru.godtools.shared.tool.parser.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
@OptIn(ExperimentalJsExport::class)
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
