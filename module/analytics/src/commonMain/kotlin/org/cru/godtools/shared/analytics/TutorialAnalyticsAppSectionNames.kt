package org.cru.godtools.shared.analytics

object TutorialAnalyticsAppSectionNames {
    const val ONBOARDING = "onboarding"

    fun forAction(action: String) = when (action) {
        TutorialAnalyticsActionNames.ONBOARDING_SKIP,
        TutorialAnalyticsActionNames.ONBOARDING_LINK_ARTICLES,
        TutorialAnalyticsActionNames.ONBOARDING_LINK_LESSONS,
        TutorialAnalyticsActionNames.ONBOARDING_LINK_TOOLS,
        TutorialAnalyticsActionNames.ONBOARDING_FINISH -> ONBOARDING
        else -> null
    }
}
