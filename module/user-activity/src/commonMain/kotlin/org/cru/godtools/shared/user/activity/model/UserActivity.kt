package org.cru.godtools.shared.user.activity.model

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
        badges = generateBadges(counters),
    )

    private companion object {
        private fun generateBadges(counters: Counters) =
            BadgeType.TOOLS_OPENED.createBadges(counters.count(TOOL_OPENS_PREFIX)) +
                BadgeType.LESSONS_COMPLETED.createBadges(counters.count(LESSON_COMPLETIONS_PREFIX)) +
                BadgeType.ARTICLES_OPENED.createBadges(counters.count(ARTICLE_OPENS_PREFIX)) +
                BadgeType.IMAGES_SHARED.createBadges(counters[IMAGE_SHARED] ?: 0) +
                BadgeType.TIPS_COMPLETED.createBadges(counters[TIPS_COMPLETED] ?: 0)
    }
}
