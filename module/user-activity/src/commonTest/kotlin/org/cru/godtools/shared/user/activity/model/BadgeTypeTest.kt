package org.cru.godtools.shared.user.activity.model

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
        assertEquals(type.variants.size, badges.size)
        type.variants.zip(badges).forEach { (size, badge) ->
            assertEquals(type, badge.type)
            assertTrue(badge.progress <= badge.target)
            assertEquals(progress.coerceAtMost(size), badge.progress)
            assertEquals(size, badge.target)
        }
    }
}
