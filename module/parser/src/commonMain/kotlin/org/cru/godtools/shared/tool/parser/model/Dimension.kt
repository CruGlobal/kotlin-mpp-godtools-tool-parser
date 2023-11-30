package org.cru.godtools.shared.tool.parser.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.concurrent.SharedImmutable
import org.cru.godtools.shared.tool.parser.util.contains

@SharedImmutable
private val REGEX_PERCENT = Regex("^(100|[0-9]{1,2}(\\.[0-9]+)?)%$")
@SharedImmutable
private val REGEX_PIXELS = Regex("^[0-9]+$")

@JsExport
@OptIn(ExperimentalJsExport::class)
sealed class Dimension {
    internal companion object {
        fun String?.toDimensionOrNull(): Dimension? {
            return when (val trimmed = this?.trim()) {
                null -> null
                in REGEX_PERCENT -> trimmed.trimEnd('%').toFloatOrNull()?.let { Percent(it / 100) }
                in REGEX_PIXELS -> trimmed.toIntOrNull()?.let { Pixels(it) }
                else -> null
            }
        }
    }

    data class Percent internal constructor(val value: Float) : Dimension()
    data class Pixels internal constructor(val value: Int) : Dimension()
}
