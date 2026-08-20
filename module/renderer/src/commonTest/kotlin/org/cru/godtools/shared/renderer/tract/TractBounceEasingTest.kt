package org.cru.godtools.shared.renderer.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

private const val DELTA = 0.000001f

class TractBounceEasingTest {
    @Test
    fun `Transform - endpoints - returns 0 at start and end`() {
        val easing = TractBounceEasing()
        assertEquals(0f, easing.transform(0f), "transform(0) should be 0")
        assertEquals(0f, easing.transform(1f), "transform(1) should be 0")
    }

    @Test
    fun `Transform - four bounces with 75 percent decay - matches known curve values`() {
        val easing = TractBounceEasing(bounces = 4, decay = 0.75)
        assertEquals(
            562.5.milliseconds,
            easing.totalDuration(300.milliseconds),
            "totalDuration(300ms) should be 562.5ms"
        )

        // first bounce
        assertEquals(0f, easing.transform(0f), DELTA, "start")
        assertEquals(0.75f, easing.transform(2f / 15), DELTA, "bounce 1 descent")
        assertEquals(1f, easing.transform(4f / 15), DELTA, "bounce 1 peak")
        assertEquals(0.75f, easing.transform(6f / 15), DELTA, "bounce 1 ascent")
        assertEquals(0f, easing.transform(8f / 15), DELTA, "bounce 1 end")

        // second bounce
        assertEquals(0.1875f, easing.transform(9f / 15), DELTA, "bounce 2 descent")
        assertEquals(0.25f, easing.transform(10f / 15), DELTA, "bounce 2 peak")
        assertEquals(0.1875f, easing.transform(11f / 15), DELTA, "bounce 2 ascent")
        assertEquals(0f, easing.transform(12f / 15), DELTA, "bounce 2 end")

        // third bounce
        assertEquals(0.046875f, easing.transform(12.5f / 15), DELTA, "bounce 3 descent")
        assertEquals(0.0625f, easing.transform(13f / 15), DELTA, "bounce 3 peak")
        assertEquals(0.046875f, easing.transform(13.5f / 15), DELTA, "bounce 3 ascent")
        assertEquals(0f, easing.transform(14f / 15), DELTA, "bounce 3 end")

        // fourth bounce
        assertEquals(0.01171875f, easing.transform(14.25f / 15), DELTA, "bounce 4 descent")
        assertEquals(0.015625f, easing.transform(14.5f / 15), DELTA, "bounce 4 peak")
        assertEquals(0.01171875f, easing.transform(14.75f / 15), DELTA, "bounce 4 ascent")
        assertEquals(0f, easing.transform(15f / 15), DELTA, "bounce 4 end")
    }

    @Test
    fun `Transform - four bounces with no decay - matches known curve values`() {
        val easing = TractBounceEasing(bounces = 4, decay = 0.0)
        assertEquals(
            1200.milliseconds,
            easing.totalDuration(300.milliseconds),
            "totalDuration(300ms) should be 1200ms"
        )

        // first bounce
        assertEquals(0f, easing.transform(0f), DELTA, "start")
        assertEquals(0.75f, easing.transform(1f / 16), DELTA, "bounce 1 descent")
        assertEquals(1f, easing.transform(2f / 16), DELTA, "bounce 1 peak")
        assertEquals(0.75f, easing.transform(3f / 16), DELTA, "bounce 1 ascent")
        assertEquals(0f, easing.transform(4f / 16), DELTA, "bounce 1 end")

        // second bounce
        assertEquals(0.75f, easing.transform(5f / 16), DELTA, "bounce 2 descent")
        assertEquals(1f, easing.transform(6f / 16), DELTA, "bounce 2 peak")
        assertEquals(0.75f, easing.transform(7f / 16), DELTA, "bounce 2 ascent")
        assertEquals(0f, easing.transform(8f / 16), DELTA, "bounce 2 end")

        // third bounce
        assertEquals(0.75f, easing.transform(9f / 16), DELTA, "bounce 3 descent")
        assertEquals(1f, easing.transform(10f / 16), DELTA, "bounce 3 peak")
        assertEquals(0.75f, easing.transform(11f / 16), DELTA, "bounce 3 ascent")
        assertEquals(0f, easing.transform(12f / 16), DELTA, "bounce 3 end")

        // fourth bounce
        assertEquals(0.75f, easing.transform(13f / 16), DELTA, "bounce 4 descent")
        assertEquals(1f, easing.transform(14f / 16), DELTA, "bounce 4 peak")
        assertEquals(0.75f, easing.transform(15f / 16), DELTA, "bounce 4 ascent")
        assertEquals(0f, easing.transform(16f / 16), DELTA, "bounce 4 end")
    }

    @Test
    fun `Transform - three bounces with 75 percent decay - matches known curve values`() {
        val easing = TractBounceEasing(bounces = 3, decay = 0.75)
        assertEquals(
            525.milliseconds,
            easing.totalDuration(300.milliseconds),
            "totalDuration(300ms) should be 525ms"
        )

        // first bounce
        assertEquals(0f, easing.transform(0f), DELTA, "start")
        assertEquals(0.75f, easing.transform(1f / 7), DELTA, "bounce 1 descent")
        assertEquals(1f, easing.transform(2f / 7), DELTA, "bounce 1 peak")
        assertEquals(0.75f, easing.transform(3f / 7), DELTA, "bounce 1 ascent")
        assertEquals(0f, easing.transform(4f / 7), DELTA, "bounce 1 end")

        // second bounce
        assertEquals(0.1875f, easing.transform(4.5f / 7), DELTA, "bounce 2 descent")
        assertEquals(0.25f, easing.transform(5f / 7), DELTA, "bounce 2 peak")
        assertEquals(0.1875f, easing.transform(5.5f / 7), DELTA, "bounce 2 ascent")
        assertEquals(0f, easing.transform(6f / 7), DELTA, "bounce 2 end")

        // third bounce
        assertEquals(0.046875f, easing.transform(6.25f / 7), DELTA, "bounce 3 descent")
        assertEquals(0.0625f, easing.transform(6.5f / 7), DELTA, "bounce 3 peak")
        assertEquals(0.046875f, easing.transform(6.75f / 7), DELTA, "bounce 3 ascent")
        assertEquals(0f, easing.transform(7f / 7), DELTA, "bounce 3 end")
    }

    @Test
    fun `totalDuration - exact duration`() {
        assertEquals(
            400.milliseconds,
            TractBounceEasing(bounces = 1, decay = 0.75).totalDuration(400.milliseconds),
            "1 bounce should equal first bounce duration"
        )
        assertEquals(
            600.milliseconds,
            TractBounceEasing(bounces = 2, decay = 0.75).totalDuration(400.milliseconds),
            "2 bounces should be exactly 600ms"
        )
        assertEquals(
            700.milliseconds,
            TractBounceEasing(bounces = 3, decay = 0.75).totalDuration(400.milliseconds),
            "3 bounces should be exactly 700ms"
        )
    }
}
