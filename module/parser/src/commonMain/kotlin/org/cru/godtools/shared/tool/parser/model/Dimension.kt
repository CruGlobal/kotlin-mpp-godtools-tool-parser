package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.VisibleForTesting
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.tool.parser.util.contains

private val REGEX_PERCENT = Regex("^(100|[0-9]{1,2}(\\.[0-9]+)?)%$")
private val REGEX_PIXELS = Regex("^[0-9]+$")

@JsExport
@OptIn(ExperimentalJsExport::class)
sealed class Dimension {
    internal companion object {
        fun String?.toDimensionOrNull() = when (val trimmed = this?.trim()) {
            null -> null
            in REGEX_PERCENT -> trimmed.trimEnd('%').toFloatOrNull()?.let { Percent(it / 100) }
            in REGEX_PIXELS -> trimmed.toIntOrNull()?.let { Pixels(it) }
            else -> null
        }
    }

    data class Percent @VisibleForTesting constructor(val value: Float) : Dimension()
    data class Pixels @VisibleForTesting constructor(val value: Int) : Dimension()
}
