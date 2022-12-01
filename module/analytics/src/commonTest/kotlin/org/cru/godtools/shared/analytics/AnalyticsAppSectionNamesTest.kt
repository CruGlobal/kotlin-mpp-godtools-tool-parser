package org.cru.godtools.shared.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    @Test
    fun testAppSectionTools() {
        assertEquals(
            AnalyticsAppSectionNames.TOOLS,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.DASHBOARD_ALL_TOOLS)
        )
    }

    @Test
    fun testAppSectionMenu() {
        assertEquals(
            AnalyticsAppSectionNames.MENU,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.SETTINGS_LANGUAGES)
        )
        assertEquals(
            AnalyticsAppSectionNames.MENU,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.SETTINGS_LANGUAGE_SELECTION)
        )
        assertEquals(
            AnalyticsAppSectionNames.MENU,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.PLATFORM_ABOUT)
        )
        assertEquals(
            AnalyticsAppSectionNames.MENU,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.PLATFORM_HELP)
        )
        assertEquals(
            AnalyticsAppSectionNames.MENU,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.PLATFORM_TERMS_OF_USE)
        )
        assertEquals(
            AnalyticsAppSectionNames.MENU,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.PLATFORM_PRIVACY_POLICY)
        )
        assertEquals(
            AnalyticsAppSectionNames.MENU,
            AnalyticsAppSectionNames.forScreen(AnalyticsScreenNames.PLATFORM_COPYRIGHT)
        )
    }

    @Test
    fun testAppSectionUnknown() {
        assertNull(AnalyticsAppSectionNames.forScreen("hjklasdhjf"))
    }
}
