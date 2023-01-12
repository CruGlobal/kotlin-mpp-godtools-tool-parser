package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.shared.user.activity.internal.test.assertEquals
import org.cru.godtools.shared.user.activity.model.Badge.BadgeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BadgeTypeTest {
    @Test
    fun testCreateBadges() {
        val type = BadgeType.TOOLS_OPENED
        val progress = 7
        val badges = type.createBadges(progress)
        assertEquals(type.variantProgressTargets.size, badges.size)
        type.variantProgressTargets.zip(badges).forEachIndexed { i, (size, badge) ->
            assertEquals(type, badge.type)
            assertEquals(i + 1, badge.variant)
            assertTrue(badge.progress <= badge.progressTarget)
            assertEquals(progress.coerceAtMost(size), badge.progress)
            assertEquals(size, badge.progressTarget)
        }
    }

    @Test
    fun testColors() {
        assertEquals(
            IconColors(
                light = RGB("#006782"),
                dark = RGB("#6ad3fa"),
                containerLight = RGB("#baeaff"),
                containerDark = RGB("#004d62"),
            ),
            BadgeType.TOOLS_OPENED.colors
        )
        assertEquals(
            IconColors(
                light = RGB("#36675b"),
                dark = RGB("#9ed1c2"),
                containerLight = RGB("#b9edde"),
                containerDark = RGB("#1d4f44"),
            ),
            BadgeType.LESSONS_COMPLETED.colors
        )
        assertEquals(
            IconColors(
                light = RGB("#555996"),
                dark = RGB("#bfc1ff"),
                containerLight = RGB("#e1e0ff"),
                containerDark = RGB("#3e417d"),
            ),
            BadgeType.ARTICLES_OPENED.colors
        )
        assertEquals(
            IconColors(
                light = RGB("#9a358c"),
                dark = RGB("#ffaceb"),
                containerLight = RGB("#ffd7f1"),
                containerDark = RGB("#7d1972"),
            ),
            BadgeType.IMAGES_SHARED.colors
        )
        assertEquals(
            IconColors(
                light = RGB("#bb0d44"),
                dark = RGB("#ffb2bb"),
                containerLight = RGB("#ffd9dc"),
                containerDark = RGB("#910032"),
            ),
            BadgeType.TIPS_COMPLETED.colors
        )
    }
}
