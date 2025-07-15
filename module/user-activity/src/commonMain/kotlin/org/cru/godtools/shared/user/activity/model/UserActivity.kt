package org.cru.godtools.shared.user.activity.model

import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.shared.user.activity.UserCounterNames.ARTICLE_OPENS_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.IMAGE_SHARED
import org.cru.godtools.shared.user.activity.UserCounterNames.LANGUAGE_USED_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.LESSON_COMPLETIONS_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.LINK_SHARED
import org.cru.godtools.shared.user.activity.UserCounterNames.SCREEN_SHARES_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.SESSION
import org.cru.godtools.shared.user.activity.UserCounterNames.SHARE_SCREEN_STARTED
import org.cru.godtools.shared.user.activity.UserCounterNames.TIPS_COMPLETED
import org.cru.godtools.shared.user.activity.UserCounterNames.TOOL_OPENS_PREFIX
import org.cru.godtools.shared.user.activity.model.Badge.BadgeType
import org.cru.godtools.shared.user.activity.util.Counters
import org.cru.godtools.shared.user.activity.util.count
import org.cru.godtools.shared.user.activity.util.sum

@ConsistentCopyVisibility
data class UserActivity private constructor(
    val toolOpens: Int = 0,
    val lessonCompletions: Int = 0,
    val screenShares: Int = 0,
    val linksShared: Int = 0,
    val languagesUsed: Int = 0,
    val sessions: Int = 0,
    val badges: List<Badge> = emptyList()
) {
    constructor(counters: Counters) : this(
        toolOpens = counters.sum(TOOL_OPENS_PREFIX),
        lessonCompletions = counters.sum(LESSON_COMPLETIONS_PREFIX),
        screenShares = counters.sum(SCREEN_SHARES_PREFIX) + (counters[SHARE_SCREEN_STARTED] ?: 0),
        linksShared = counters[LINK_SHARED] ?: 0,
        languagesUsed = counters.count(LANGUAGE_USED_PREFIX),
        sessions = counters[SESSION] ?: 0,
        badges = generateBadges(counters).sortedByDescending { it.isEarned },
    )

    private companion object {
        private fun generateBadges(counters: Counters) =
            BadgeType.TOOLS_OPENED.createBadges(counters.count(TOOL_OPENS_PREFIX)) +
                BadgeType.LESSONS_COMPLETED.createBadges(counters.count(LESSON_COMPLETIONS_PREFIX)) +
                BadgeType.ARTICLES_OPENED.createBadges(counters.count(ARTICLE_OPENS_PREFIX)) +
                BadgeType.IMAGES_SHARED.createBadges(counters[IMAGE_SHARED] ?: 0) +
                BadgeType.TIPS_COMPLETED.createBadges(counters[TIPS_COMPLETED] ?: 0)
    }

    object Colors {
        val toolOpens = IconColors(base = RGB("#05699B"))
        val lessonCompletions = IconColors(base = RGB("#A4D7C8"))
        val screenShares = IconColors(base = RGB("#E55B36"))
        val linksShared = IconColors(base = RGB("#2F3676"))
        val languagesUsed = IconColors(base = RGB("#CEFFC1"))
        val sessions = IconColors(base = RGB("#E0CE26"))
    }
}
