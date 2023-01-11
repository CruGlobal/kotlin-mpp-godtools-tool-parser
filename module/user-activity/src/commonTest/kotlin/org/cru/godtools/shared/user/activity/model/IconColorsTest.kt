package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.shared.common.model.ThemeType
import org.cru.godtools.shared.common.model.toPlatformColor
import org.cru.godtools.shared.user.activity.internal.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class IconColorsTest {
    private companion object {
        val RED = RGB("F00")
        val GREEN = RGB("0F0")
        val BLUE = RGB("00F")
        val BLACK = RGB("000")
    }

    @Test
    fun testIconColorsTonalPaletteGeneration() {
        assertEquals(
            IconColors(
                light = RGB("#006494"),
                dark = RGB("#8fcdff"),
                containerLight = RGB("#cbe6ff"),
                containerDark = RGB("#004b71"),
            ),
            IconColors(RGB("#05699B"))
        )
    }

    @Test
    fun testColor() {
        val colors = IconColors(light = RED, dark = GREEN, containerLight = BLACK, containerDark = BLACK)

        assertEquals(RED.toPlatformColor(), colors.color(ThemeType.LIGHT))
        assertEquals(GREEN.toPlatformColor(), colors.color(ThemeType.DARK))
    }

    @Test
    fun testContainerColor() {
        val colors = IconColors(light = BLACK, dark = BLACK, containerLight = RED, containerDark = GREEN)

        assertEquals(RED.toPlatformColor(), colors.containerColor(ThemeType.LIGHT))
        assertEquals(GREEN.toPlatformColor(), colors.containerColor(ThemeType.DARK))
    }

    @Test
    fun testAlpha() {
        val alpha = 0.5f
        val colors = IconColors(light = RED, dark = GREEN, containerLight = BLUE, containerDark = BLACK)
        val alphaColors = colors.alpha(alpha)

        assertEquals(alpha, alphaColors.light.alpha)
        assertEquals(alpha, alphaColors.dark.alpha)
        assertEquals(alpha, alphaColors.containerLight.alpha)
        assertEquals(alpha, alphaColors.containerDark.alpha)
    }
}
