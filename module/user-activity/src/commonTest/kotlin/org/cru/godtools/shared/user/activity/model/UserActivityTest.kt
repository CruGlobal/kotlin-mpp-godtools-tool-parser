package org.cru.godtools.shared.user.activity.model

import org.cru.godtools.shared.user.activity.UserCounterNames.LANGUAGE_USED
import org.cru.godtools.shared.user.activity.UserCounterNames.LESSON_COMPLETIONS_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.LESSON_OPEN
import org.cru.godtools.shared.user.activity.UserCounterNames.LINK_SHARED
import org.cru.godtools.shared.user.activity.UserCounterNames.SCREEN_SHARE
import org.cru.godtools.shared.user.activity.UserCounterNames.SESSION
import org.cru.godtools.shared.user.activity.UserCounterNames.SHARE_SCREEN_STARTED
import org.cru.godtools.shared.user.activity.UserCounterNames.TOOL_OPEN
import kotlin.test.Test
import kotlin.test.assertEquals

class UserActivityTest {
    @Test
    fun testUserActivityDefaults() {
        val activity = UserActivity(emptyMap())
        assertEquals(0, activity.toolOpens)
        assertEquals(0, activity.lessonCompletions)
        assertEquals(0, activity.screenShares)
        assertEquals(0, activity.linksShared)
        assertEquals(0, activity.languagesUsed)
        assertEquals(0, activity.sessions)
    }

    @Test
    fun testUserActivityToolOpens() {
        val counters = mutableMapOf<String, Int>()
        assertEquals(0, UserActivity(counters).toolOpens)

        counters[TOOL_OPEN("kgp")] = 5
        assertEquals(5, UserActivity(counters).toolOpens)

        counters[TOOL_OPEN("fourlaws")] = 3
        assertEquals(8, UserActivity(counters).toolOpens)

        counters[LESSON_OPEN("lessonhs")] = 7
        assertEquals(8, UserActivity(counters).toolOpens)
    }

    @Test
    fun testUserActivityLessonCompletions() {
        val counters = mutableMapOf<String, Int>()
        assertEquals(0, UserActivity(counters).lessonCompletions)

        counters["${LESSON_COMPLETIONS_PREFIX}lessonhs"] = 5
        assertEquals(5, UserActivity(counters).lessonCompletions)

        counters["${LESSON_COMPLETIONS_PREFIX}lesson2"] = 3
        assertEquals(8, UserActivity(counters).lessonCompletions)

        counters[TOOL_OPEN("lessonhs")] = 7
        assertEquals(8, UserActivity(counters).lessonCompletions)
    }

    @Test
    fun testUserActivityScreenShares() {
        val counters = mutableMapOf<String, Int>()
        assertEquals(0, UserActivity(counters).screenShares)

        counters[SCREEN_SHARE("kgp")] = 5
        assertEquals(5, UserActivity(counters).screenShares)

        counters[SCREEN_SHARE("fourlaws")] = 3
        assertEquals(8, UserActivity(counters).screenShares)

        // legacy counter on Android
        counters[SHARE_SCREEN_STARTED] = 7
        assertEquals(15, UserActivity(counters).screenShares)

        counters[TOOL_OPEN("lessonhs")] = 11
        assertEquals(15, UserActivity(counters).screenShares)
    }

    @Test
    fun testUserActivityLinksShared() {
        val counters = mutableMapOf<String, Int>()
        assertEquals(0, UserActivity(counters).linksShared)

        counters[LINK_SHARED] = 5
        assertEquals(5, UserActivity(counters).linksShared)

        counters[TOOL_OPEN("lessonhs")] = 11
        assertEquals(5, UserActivity(counters).linksShared)
    }

    @Test
    fun testUserActivityLanguagesUsed() {
        val counters = mutableMapOf<String, Int>()
        assertEquals(0, UserActivity(counters).languagesUsed)

        counters[LANGUAGE_USED("en")] = 5
        assertEquals(1, UserActivity(counters).languagesUsed)

        counters[LANGUAGE_USED("fr")] = 3
        assertEquals(2, UserActivity(counters).languagesUsed)

        counters[LANGUAGE_USED("es")] = 0
        assertEquals(2, UserActivity(counters).languagesUsed)

        counters[TOOL_OPEN("lessonhs")] = 11
        assertEquals(2, UserActivity(counters).languagesUsed)
    }

    @Test
    fun testUserActivitySessions() {
        val counters = mutableMapOf<String, Int>()
        assertEquals(0, UserActivity(counters).sessions)

        counters[SESSION] = 3
        assertEquals(3, UserActivity(counters).sessions)

        counters[LANGUAGE_USED("fr")] = 3
        assertEquals(3, UserActivity(counters).sessions)
    }
}
