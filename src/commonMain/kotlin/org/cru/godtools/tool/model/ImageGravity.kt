package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.internal.RestrictTo
import splitties.bitflags.minusFlag
import splitties.bitflags.withFlag
import kotlin.jvm.JvmInline

private const val XML_START = "start"
private const val XML_END = "end"
private const val XML_TOP = "top"
private const val XML_BOTTOM = "bottom"
private const val XML_CENTER = "center"

private const val BIT_START = 1 shl 0
private const val BIT_END = 1 shl 1
private const val BIT_TOP = 1 shl 2
private const val BIT_BOTTOM = 1 shl 3

private const val BIT_CENTER_X = BIT_START or BIT_END
private const val BIT_CENTER_Y = BIT_TOP or BIT_BOTTOM
private const val BIT_CENTER = BIT_CENTER_X or BIT_CENTER_Y

private const val MASK_X_AXIS = BIT_START or BIT_END or BIT_CENTER_X
private const val MASK_Y_AXIS = BIT_TOP or BIT_BOTTOM or BIT_CENTER_Y

@JvmInline
value class ImageGravity(private val gravity: Int) {
    val isCenter get() = gravity and (MASK_X_AXIS or MASK_Y_AXIS) == BIT_CENTER
    val isCenterX get() = gravity and MASK_X_AXIS == BIT_CENTER_X
    val isCenterY get() = gravity and MASK_Y_AXIS == BIT_CENTER_Y
    val isStart get() = gravity and MASK_X_AXIS == BIT_START
    val isEnd get() = gravity and MASK_X_AXIS == BIT_END
    val isTop get() = gravity and MASK_Y_AXIS == BIT_TOP
    val isBottom get() = gravity and MASK_Y_AXIS == BIT_BOTTOM

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal infix fun or(other: ImageGravity) = ImageGravity(gravity or other.gravity)

    companion object {
        @RestrictTo(RestrictTo.Scope.TESTS)
        internal val END = ImageGravity(BIT_END)
        @RestrictTo(RestrictTo.Scope.TESTS)
        internal val TOP = ImageGravity(BIT_TOP)
        internal val CENTER = ImageGravity(BIT_CENTER)

        internal fun String.toImageGravityOrNull() = try {
            var gravity = BIT_CENTER
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
                    XML_CENTER -> {
                        if (!seenX) gravity = gravity.withFlag(BIT_CENTER_X)
                        if (!seenY) gravity = gravity.withFlag(BIT_CENTER_Y)
                    }
                }
            }

            ImageGravity(gravity)
        } catch (e: IllegalArgumentException) {
            Napier.e(tag = "ImageGravity", throwable = e, message = { "error parsing ImageGravity: $this" })
            null
        }
    }
}
