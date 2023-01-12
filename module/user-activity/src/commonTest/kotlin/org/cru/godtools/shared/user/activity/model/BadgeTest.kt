package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.shared.user.activity.internal.test.assertEquals
import org.cru.godtools.shared.user.activity.model.Badge.BadgeType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BadgeTest {
    @Test
    fun testIsEarned() {
        assertFalse(Badge(BadgeType.TOOLS_OPENED, 1, 4, 5).isEarned)
        assertTrue(Badge(BadgeType.TOOLS_OPENED, 1, 5, 5).isEarned)
        assertTrue(Badge(BadgeType.TOOLS_OPENED, 1, 6, 5).isEarned)
    }

    @Test
    fun testColors() {
        val notEarned = Badge(BadgeType.TOOLS_OPENED, 1, 1, 2)
        assertEquals(Badge.COLORS_NOT_EARNED, notEarned.colors)

        val earned = Badge(BadgeType.TOOLS_OPENED, 1, 2, 2)
        assertEquals(BadgeType.TOOLS_OPENED.colors, earned.colors)
    }

    @Test
    fun testColorsNotEarned() {
        assertEquals(
            IconColors(
                light = RGB("#5d5e5f61"),
                dark = RGB("#c6c6c661"),
                containerLight = RGB("#e2e2e261"),
                containerDark = RGB("#45474761"),
            ),
            Badge.COLORS_NOT_EARNED
        )
    }
}
