package org.cru.godtools.shared.renderer.tract

import androidx.compose.animation.core.Easing
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration

/**
 * Easing that bounces [bounces] times, each bounce's height decaying by [decay].
 * Output is the bounce height fraction (0 = rest position, 1 = full bounce height);
 * it starts and ends at 0.
 */
internal class TractBounceEasing(private val bounces: Int = 4, decay: Double = 0.5) : Easing {
    private val heightDecay = 1 - decay
    private val timeDecay = sqrt(heightDecay)
    private val totalTime = (0 until bounces).sumOf { timeDecay.pow(it) }

    fun totalDuration(firstBounceDuration: Duration) = firstBounceDuration * totalTime

    override fun transform(fraction: Float): Float {
        if (fraction <= 0 || fraction >= 1) return 0f

        // determine which bounce this is (and the x offset)
        var inputOffset = 0.0
        var bounce = 0
        while (bounce < bounces) {
            val bounceDuration = timeDecay.pow(bounce) / totalTime
            if (fraction <= inputOffset + bounceDuration) {
                // current bounce, center the quadratic for this bounce and quit looping
                inputOffset += bounceDuration / 2
                break
            }
            inputOffset += bounceDuration
            bounce++
        }

        // base quadratic "-4x^2" shifted & scaled to fill each bounce's segment
        val x = fraction - inputOffset
        val q = -4 * x * x
        val output = q * totalTime * totalTime
        return (output + heightDecay.pow(bounce)).toFloat()
    }
}
