package org.cru.godtools.shared.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyticsAppSectionNamesTest {
    @Test
    fun testAppSectionAccount() {
        assertEquals(
            AnalyticsAppSectionNames.ACCOUNT,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.ACCOUNT_ACTIVITY)
        )
        assertEquals(
            AnalyticsAppSectionNames.ACCOUNT,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.ACCOUNT_GLOBAL_ACTIVITY)
        )
    }
}
