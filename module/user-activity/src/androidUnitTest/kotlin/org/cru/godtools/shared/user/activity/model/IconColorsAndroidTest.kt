package org.cru.godtools.shared.user.activity.model

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import org.cru.godtools.shared.common.model.ThemeType
import org.cru.godtools.shared.user.activity.model.IconColorsTest.Companion.BLACK
import org.cru.godtools.shared.user.activity.model.IconColorsTest.Companion.GREEN
import org.cru.godtools.shared.user.activity.model.IconColorsTest.Companion.RED

class IconColorsAndroidTest {
    @Test
    fun testColorExtension() {
        val colors = IconColors(light = RED, dark = GREEN, containerLight = BLACK, containerDark = BLACK)

        assertEquals(Color(1f, 0f, 0f), colors.color(ThemeType.LIGHT))
        assertEquals(Color(0f, 1f, 0f), colors.color(ThemeType.DARK))
    }

    @Test
    fun testContainerColorExtension() {
        val colors = IconColors(light = BLACK, dark = BLACK, containerLight = RED, containerDark = GREEN)

        assertEquals(Color(1f, 0f, 0f), colors.containerColor(ThemeType.LIGHT))
        assertEquals(Color(0f, 1f, 0f), colors.containerColor(ThemeType.DARK))
    }
}
