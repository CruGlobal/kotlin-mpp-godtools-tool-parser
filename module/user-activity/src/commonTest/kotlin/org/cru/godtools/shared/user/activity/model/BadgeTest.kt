package org.cru.godtools.shared.user.activity.model

import org.cru.godtools.shared.user.activity.model.Badge.BadgeType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BadgeTest {
    @Test
    fun testIsEarned() {
        assertFalse(Badge(BadgeType.TOOLS_OPENED, 4, 5).isEarned)
        assertTrue(Badge(BadgeType.TOOLS_OPENED, 5, 5).isEarned)
        assertTrue(Badge(BadgeType.TOOLS_OPENED, 6, 5).isEarned)
    }
}
