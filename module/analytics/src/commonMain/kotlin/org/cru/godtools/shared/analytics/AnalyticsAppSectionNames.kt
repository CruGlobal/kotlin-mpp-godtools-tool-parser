package org.cru.godtools.shared.analytics

import org.ccci.gto.support.androidx.annotation.VisibleForTesting

object AnalyticsAppSectionNames {
    @VisibleForTesting
    internal const val MENU = "menu"
    @VisibleForTesting
    internal const val ACCOUNT = "account"
    const val TOOLS = "tools"
    private const val SUB_SECTION_LANGUAGE_SETTINGS = "language settings"

    fun forScreen(name: String) = when (name) {
        AnalyticsScreenNames.DASHBOARD_ALL_TOOLS -> TOOLS
        AnalyticsScreenNames.ACCOUNT_ACTIVITY,
        AnalyticsScreenNames.ACCOUNT_GLOBAL_ACTIVITY -> ACCOUNT
        AnalyticsScreenNames.SETTINGS_LANGUAGES,
        AnalyticsScreenNames.SETTINGS_LANGUAGE_SELECTION,
        AnalyticsScreenNames.PLATFORM_ABOUT,
        AnalyticsScreenNames.PLATFORM_HELP,
        AnalyticsScreenNames.PLATFORM_TERMS_OF_USE,
        AnalyticsScreenNames.PLATFORM_PRIVACY_POLICY,
        AnalyticsScreenNames.PLATFORM_COPYRIGHT -> MENU

        // These should have been action events, but are handled as screen events 🤦
        AnalyticsActionNames.PLATFORM_CONTACT_US,
        AnalyticsActionNames.PLATFORM_SHARE_GODTOOLS,
        AnalyticsActionNames.PLATFORM_SHARE_STORY -> MENU

        else -> null
    }

    fun subSectionForScreen(name: String) = when (name) {
        AnalyticsScreenNames.SETTINGS_LANGUAGE_SELECTION -> SUB_SECTION_LANGUAGE_SETTINGS
        else -> null
    }
}
