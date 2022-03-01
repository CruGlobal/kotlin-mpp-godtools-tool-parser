package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.internal.VisibleForTesting

private const val XML_START = "start"
private const val XML_END = "end"
private const val XML_TOP = "top"
private const val XML_BOTTOM = "bottom"
private const val XML_CENTER = "center"

class Gravity @VisibleForTesting constructor(val horizontal: Horizontal, val vertical: Vertical) {
    val isStart get() = horizontal == Horizontal.START
    val isEnd get() = horizontal == Horizontal.END
    val isCenterX get() = horizontal == Horizontal.CENTER
    val isTop get() = vertical == Vertical.TOP
    val isBottom get() = vertical == Vertical.BOTTOM
    val isCenterY get() = vertical == Vertical.CENTER
    val isCenter get() = isCenterX && isCenterY

    companion object {
        val CENTER = Gravity(Horizontal.CENTER, Vertical.CENTER)

        internal fun String?.toGravityOrNull(): Gravity? {
            if (this == null) return null
            try {
                var horizontal: Horizontal? = null
                var vertical: Vertical? = null
                REGEX_SEQUENCE_SEPARATOR.split(this).forEach {
                    when (it) {
                        XML_START -> {
                            require(horizontal == null) { "multiple X-Axis gravities in: $this" }
                            horizontal = Horizontal.START
                        }
                        XML_END -> {
                            require(horizontal == null) { "multiple X-Axis gravities in: $this" }
                            horizontal = Horizontal.END
                        }
                        XML_TOP -> {
                            require(vertical == null) { "multiple Y-Axis gravities in: $this" }
                            vertical = Vertical.TOP
                        }
                        XML_BOTTOM -> {
                            require(vertical == null) { "multiple Y-Axis gravities in: $this" }
                            vertical = Vertical.BOTTOM
                        }
                        XML_CENTER -> Unit
                    }
                }

                return Gravity(horizontal ?: Horizontal.CENTER, vertical ?: Vertical.CENTER)
            } catch (e: IllegalArgumentException) {
                Napier.e(tag = "Gravity", throwable = e, message = { "error parsing Gravity: $this" })
                return null
            }
        }
    }

    enum class Horizontal { START, CENTER, END }
    enum class Vertical { TOP, CENTER, BOTTOM }
}
