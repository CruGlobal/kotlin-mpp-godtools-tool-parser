package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import splitties.bitflags.minusFlag
import splitties.bitflags.withFlag

private const val XML_START = "start"
private const val XML_END = "end"
private const val XML_TOP = "top"
private const val XML_BOTTOM = "bottom"
private const val XML_CENTER = "center"

private const val BIT_START = 1 shl 0
private const val BIT_END = 1 shl 1
private const val BIT_TOP = 1 shl 2
private const val BIT_BOTTOM = 1 shl 3

private const val MASK_X_AXIS = BIT_START or BIT_END
private const val MASK_Y_AXIS = BIT_TOP or BIT_BOTTOM

class Gravity internal constructor(private val gravity: Int) {
    val isStart get() = gravity and MASK_X_AXIS == BIT_START
    val isEnd get() = gravity and MASK_X_AXIS == BIT_END
    val isCenterX get() = gravity and MASK_X_AXIS == 0
    val isTop get() = gravity and MASK_Y_AXIS == BIT_TOP
    val isBottom get() = gravity and MASK_Y_AXIS == BIT_BOTTOM
    val isCenterY get() = gravity and MASK_Y_AXIS == 0
    val isCenter get() = gravity and (MASK_X_AXIS or MASK_Y_AXIS) == 0

    val horizontal
        get() = when {
            isStart -> Horizontal.START
            isEnd -> Horizontal.END
            else -> Horizontal.CENTER
        }
    val vertical
        get() = when {
            isTop -> Vertical.TOP
            isBottom -> Vertical.BOTTOM
            else -> Vertical.CENTER
        }

    companion object {
        val CENTER = Gravity(0)
        internal val START = Gravity(BIT_START)

        internal fun String?.toGravityOrNull(): Gravity? {
            if (this == null) return null
            try {
                var gravity = 0
                var seenX = false
                var seenY = false
                REGEX_SEQUENCE_SEPARATOR.split(this).forEach {
                    when (it) {
                        XML_START -> {
                            require(!seenX) { "multiple X-Axis gravities in: $this" }
                            gravity = gravity.minusFlag(MASK_X_AXIS).withFlag(BIT_START)
                            seenX = true
                        }
                        XML_END -> {
                            require(!seenX) { "multiple X-Axis gravities in: $this" }
                            gravity = gravity.minusFlag(MASK_X_AXIS).withFlag(BIT_END)
                            seenX = true
                        }
                        XML_TOP -> {
                            require(!seenY) { "multiple Y-Axis gravities in: $this" }
                            gravity = gravity.minusFlag(MASK_Y_AXIS).withFlag(BIT_TOP)
                            seenY = true
                        }
                        XML_BOTTOM -> {
                            require(!seenY) { "multiple Y-Axis gravities in: $this" }
                            gravity = gravity.minusFlag(MASK_Y_AXIS).withFlag(BIT_BOTTOM)
                            seenY = true
                        }
                        XML_CENTER -> Unit
                    }
                }

                return Gravity(gravity)
            } catch (e: IllegalArgumentException) {
                Napier.e(tag = "Gravity", throwable = e, message = { "error parsing Gravity: $this" })
                return null
            }
        }
    }

    enum class Horizontal { START, CENTER, END }
    enum class Vertical { TOP, CENTER, BOTTOM }
}
