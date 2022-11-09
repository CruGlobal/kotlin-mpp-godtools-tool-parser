package org.cru.godtools.shared.user.activity

import io.fluidsonic.locale.Locale
import org.ccci.gto.support.fluidsonic.locale.PlatformLocale
import org.ccci.gto.support.fluidsonic.locale.toCommon

@Suppress("FunctionName")
object UserCounterNames {
    const val SESSION = "sessions"
    const val LINK_SHARED = "link_shares"
    const val IMAGE_SHARED = "image_shares"
    const val TIPS_COMPLETED = "tips_completed"
    internal const val LANGUAGE_USED_PREFIX = "language_used."
    private const val LESSON_OPENS_PREFIX = "lesson_opens."
    internal const val LESSON_COMPLETIONS_PREFIX = "lesson_completions."
    internal const val SCREEN_SHARES_PREFIX = "screen_shares."
    internal const val SHARE_SCREEN_STARTED = "share_screen_engaged"
    internal const val TOOL_OPENS_PREFIX = "tool_opens."

    fun LESSON_OPEN(tool: String) = "$LESSON_OPENS_PREFIX$tool".lowercase()
    fun TOOL_OPEN(tool: String) = "$TOOL_OPENS_PREFIX$tool".lowercase()
    fun SCREEN_SHARE(tool: String) = "$SCREEN_SHARES_PREFIX$tool".lowercase()
    fun LANGUAGE_USED(locale: PlatformLocale) = LANGUAGE_USED(locale.toCommon())
    fun LANGUAGE_USED(locale: String) = LANGUAGE_USED(Locale.forLanguageTag(locale))
    private fun LANGUAGE_USED(locale: Locale) = "$LANGUAGE_USED_PREFIX$locale".lowercase()
}
