package org.cru.godtools.shared.user.activity.model

import io.fluidsonic.locale.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.ccci.gto.support.fluidsonic.locale.toPlatform
import org.cru.godtools.shared.common.model.toUriOrNull
import org.cru.godtools.shared.user.activity.UserCounterNames.ARTICLE_OPEN
import org.cru.godtools.shared.user.activity.UserCounterNames.IMAGE_SHARED
import org.cru.godtools.shared.user.activity.UserCounterNames.LANGUAGE_USED
import org.cru.godtools.shared.user.activity.UserCounterNames.LESSON_COMPLETIONS_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.LESSON_OPEN
import org.cru.godtools.shared.user.activity.UserCounterNames.LINK_SHARED
import org.cru.godtools.shared.user.activity.UserCounterNames.SCREEN_SHARE
import org.cru.godtools.shared.user.activity.UserCounterNames.SESSION
import org.cru.godtools.shared.user.activity.UserCounterNames.SHARE_SCREEN_STARTED
import org.cru.godtools.shared.user.activity.UserCounterNames.TIPS_COMPLETED
import org.cru.godtools.shared.user.activity.UserCounterNames.TOOL_OPEN

@RunOnAndroidWith(AndroidJUnit4::class)
class UserActivityTest {
    private val counters = mutableMapOf<String, Int>()

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
        assertEquals(0, UserActivity(counters).linksShared)

        counters[LINK_SHARED] = 5
        assertEquals(5, UserActivity(counters).linksShared)

        counters[TOOL_OPEN("lessonhs")] = 11
        assertEquals(5, UserActivity(counters).linksShared)
    }

    @Test
    fun testUserActivityLanguagesUsed() {
        assertEquals(0, UserActivity(counters).languagesUsed)

        counters[LANGUAGE_USED(Locale.forLanguageTag("en").toPlatform())] = 5
        assertEquals(1, UserActivity(counters).languagesUsed)

        counters[LANGUAGE_USED(Locale.forLanguageTag("fr").toPlatform())] = 3
        assertEquals(2, UserActivity(counters).languagesUsed)

        counters[LANGUAGE_USED(Locale.forLanguageTag("es").toPlatform())] = 0
        assertEquals(2, UserActivity(counters).languagesUsed)

        counters[TOOL_OPEN("lessonhs")] = 11
        assertEquals(2, UserActivity(counters).languagesUsed)
    }

    @Test
    fun testUserActivitySessions() {
        assertEquals(0, UserActivity(counters).sessions)

        counters[SESSION] = 3
        assertEquals(3, UserActivity(counters).sessions)

        counters[LANGUAGE_USED(Locale.forLanguageTag("fr").toPlatform())] = 3
        assertEquals(3, UserActivity(counters).sessions)
    }

    @Test
    fun testUserActivityBadgesToolsOpened() {
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.TOOLS_OPENED }.forEach {
            assertEquals(0.coerceAtMost(it.progressTarget), it.progress)
        }

        counters[TOOL_OPEN("kgp")] = 5
        counters[TOOL_OPEN("fourlaws")] = 1
        counters[TOOL_OPEN("satisfied")] = 0
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.TOOLS_OPENED }.forEach {
            assertEquals(
                2.coerceAtMost(it.progressTarget),
                it.progress,
                "should be 2 tool opens because 0 opens should be excluded"
            )
        }
    }

    @Test
    fun testUserActivityBadgesLessonsCompleted() {
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.LESSONS_COMPLETED }.forEach {
            assertEquals(0.coerceAtMost(it.progressTarget), it.progress)
        }

        counters[LESSON_COMPLETIONS_PREFIX + "a"] = 5
        counters[LESSON_COMPLETIONS_PREFIX + "b"] = 1
        counters[LESSON_COMPLETIONS_PREFIX + "c"] = 0
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.LESSONS_COMPLETED }.forEach {
            assertEquals(
                2.coerceAtMost(it.progressTarget),
                it.progress,
                "should be 2 lesson completions because 0 completions should be excluded"
            )
        }
    }

    @Test
    fun testUserActivityBadgesArticlesOpened() {
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.ARTICLES_OPENED }.forEach {
            assertEquals(0.coerceAtMost(it.progressTarget), it.progress)
        }

        counters[ARTICLE_OPEN("https://example.com/a".toUriOrNull()!!)] = 5
        counters[ARTICLE_OPEN("https://example.com/b".toUriOrNull()!!)] = 1
        counters[ARTICLE_OPEN("https://example.com/c".toUriOrNull()!!)] = 0
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.ARTICLES_OPENED }.forEach {
            assertEquals(
                2.coerceAtMost(it.progressTarget),
                it.progress,
                "should be 2 lesson completions because 0 completions should be excluded"
            )
        }
    }

    @Test
    fun testUserActivityBadgesImagesShared() {
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.IMAGES_SHARED }.forEach {
            assertEquals(0.coerceAtMost(it.progressTarget), it.progress)
        }

        counters[IMAGE_SHARED] = 5
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.IMAGES_SHARED }.forEach {
            assertEquals(5.coerceAtMost(it.progressTarget), it.progress)
        }
    }

    @Test
    fun testUserActivityBadgesTipsCompleted() {
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.TIPS_COMPLETED }.forEach {
            assertEquals(0.coerceAtMost(it.progressTarget), it.progress)
        }

        counters[TIPS_COMPLETED] = 5
        UserActivity(counters).badges.filter { it.type == Badge.BadgeType.TIPS_COMPLETED }.forEach {
            assertEquals(5.coerceAtMost(it.progressTarget), it.progress)
        }
    }

    @Test
    fun testUserActivityBadgesSorting() {
        counters[TOOL_OPEN("kgp")] = 5
        counters[ARTICLE_OPEN("https://example.com/a".toUriOrNull()!!)] = 5
        counters[TIPS_COMPLETED] = 5

        val badges = UserActivity(counters).badges
        assertTrue(badges.take(3).all { it.isEarned })
        assertTrue(badges.drop(3).none { it.isEarned })
    }

    @Test
    fun testEquals() {
        assertEquals(UserActivity(emptyMap()), UserActivity(emptyMap()))
    }
}
