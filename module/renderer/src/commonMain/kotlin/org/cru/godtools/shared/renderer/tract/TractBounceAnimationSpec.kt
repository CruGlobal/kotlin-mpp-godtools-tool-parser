package org.cru.godtools.shared.renderer.tract

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedAnimationSpec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class TractBounceAnimationSpec(
    private val heightPx: Float,
    firstBounceDuration: Duration = 400.milliseconds,
    bounces: Int = 4,
    decay: Double = 0.5,
) : AnimationSpec<Float> {
    private val bounceEasing = TractBounceEasing(bounces, decay)
    private val bounceDuration = bounceEasing.totalDuration(firstBounceDuration).inWholeNanoseconds

    override fun <V : AnimationVector> vectorize(converter: TwoWayConverter<Float, V>): VectorizedAnimationSpec<V> =
        object : VectorizedAnimationSpec<V> {
            override val isInfinite = false

            override fun getDurationNanos(initialValue: V, targetValue: V, initialVelocity: V) = bounceDuration

            override fun getValueFromNanos(playTimeNanos: Long, initialValue: V, targetValue: V, initialVelocity: V) =
                converter.convertToVector(valueAt(playTimeNanos, converter.convertFromVector(targetValue)))

            override fun getVelocityFromNanos(
                playTimeNanos: Long,
                initialValue: V,
                targetValue: V,
                initialVelocity: V,
            ) = converter.convertToVector(0f)
        }

    private fun valueAt(playTimeNanos: Long, target: Float): Float {
        val fraction = (playTimeNanos.toFloat() / bounceDuration).coerceIn(0f, 1f)
        return target - heightPx * bounceEasing.transform(fraction)
    }
}
