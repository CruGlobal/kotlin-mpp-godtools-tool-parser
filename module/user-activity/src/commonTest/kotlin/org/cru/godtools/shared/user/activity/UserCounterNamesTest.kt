package org.cru.godtools.shared.user.activity

import io.fluidsonic.locale.Locale
import org.ccci.gto.support.fluidsonic.locale.toPlatform
import kotlin.test.Test
import kotlin.test.assertEquals

class UserCounterNamesTest {
    @Test
    fun verifyCounterNames() {
        assertEquals("tool_opens.kgp", UserCounterNames.TOOL_OPEN("kgp"))
        assertEquals("lesson_opens.lessonhs", UserCounterNames.LESSON_OPEN("lessonhs"))
        assertEquals("screen_shares.kgp", UserCounterNames.SCREEN_SHARE("kgp"))
        assertEquals("language_used.en", UserCounterNames.LANGUAGE_USED(Locale.forLanguageTag("en").toPlatform()))
    }
}
