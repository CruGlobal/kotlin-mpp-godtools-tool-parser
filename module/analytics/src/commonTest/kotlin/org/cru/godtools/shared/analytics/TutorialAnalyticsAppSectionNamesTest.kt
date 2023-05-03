package org.cru.godtools.shared.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TutorialAnalyticsAppSectionNamesTest {
    @Test
    fun testAppSectionOnboarding() {
        assertEquals(
            TutorialAnalyticsAppSectionNames.ONBOARDING,
            TutorialAnalyticsAppSectionNames.forAction(TutorialAnalyticsActionNames.ONBOARDING_SKIP),
        )
        assertEquals(
            TutorialAnalyticsAppSectionNames.ONBOARDING,
            TutorialAnalyticsAppSectionNames.forAction(TutorialAnalyticsActionNames.ONBOARDING_LINK_ARTICLES),
        )
        assertEquals(
            TutorialAnalyticsAppSectionNames.ONBOARDING,
            TutorialAnalyticsAppSectionNames.forAction(TutorialAnalyticsActionNames.ONBOARDING_LINK_LESSONS),
        )
        assertEquals(
            TutorialAnalyticsAppSectionNames.ONBOARDING,
            TutorialAnalyticsAppSectionNames.forAction(TutorialAnalyticsActionNames.ONBOARDING_LINK_TOOLS),
        )
        assertEquals(
            TutorialAnalyticsAppSectionNames.ONBOARDING,
            TutorialAnalyticsAppSectionNames.forAction(TutorialAnalyticsActionNames.ONBOARDING_FINISH),
        )
    }

    @Test
    fun testAppSectionUnrecognized() {
        assertNull(TutorialAnalyticsAppSectionNames.forAction("ajlksdfjklawer"))
    }
}
