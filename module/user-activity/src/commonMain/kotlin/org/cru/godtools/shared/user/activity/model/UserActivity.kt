package org.cru.godtools.shared.user.activity.model

import org.cru.godtools.shared.user.activity.UserCounterNames.LANGUAGE_USED_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.LESSON_COMPLETIONS_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.LINK_SHARED
import org.cru.godtools.shared.user.activity.UserCounterNames.SCREEN_SHARES_PREFIX
import org.cru.godtools.shared.user.activity.UserCounterNames.SESSION
import org.cru.godtools.shared.user.activity.UserCounterNames.SHARE_SCREEN_STARTED
import org.cru.godtools.shared.user.activity.UserCounterNames.TOOL_OPENS_PREFIX
import org.cru.godtools.shared.user.activity.util.Counters
import org.cru.godtools.shared.user.activity.util.sum

data class UserActivity private constructor(
    val toolOpens: Int = 0,
    val lessonCompletions: Int = 0,
    val screenShares: Int = 0,
    val linksShared: Int = 0,
    val languagesUsed: Int = 0,
    val sessions: Int = 0,
) {
    constructor(counters: Counters) : this(
        toolOpens = counters.sum(TOOL_OPENS_PREFIX),
        lessonCompletions = counters.sum(LESSON_COMPLETIONS_PREFIX),
        screenShares = counters.sum(SCREEN_SHARES_PREFIX) + (counters[SHARE_SCREEN_STARTED] ?: 0),
        linksShared = counters[LINK_SHARED] ?: 0,
        languagesUsed = counters.count { (counter, count) -> counter.startsWith(LANGUAGE_USED_PREFIX) && count > 0 },
        sessions = counters[SESSION] ?: 0
    )
}
