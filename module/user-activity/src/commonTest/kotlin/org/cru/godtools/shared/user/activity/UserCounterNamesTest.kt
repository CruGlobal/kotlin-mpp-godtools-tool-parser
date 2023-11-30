package org.cru.godtools.shared.user.activity

import io.fluidsonic.locale.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.ccci.gto.support.fluidsonic.locale.toPlatform
import org.cru.godtools.shared.common.model.toUriOrNull

@RunOnAndroidWith(AndroidJUnit4::class)
class UserCounterNamesTest {
    @Test
    fun verifyCounterNames() {
        assertEquals("tool_opens.kgp", UserCounterNames.TOOL_OPEN("kgp"))
        assertEquals("lesson_opens.lessonhs", UserCounterNames.LESSON_OPEN("lessonhs"))
        assertEquals("screen_shares.kgp", UserCounterNames.SCREEN_SHARE("kgp"))
        assertEquals("language_used.en", UserCounterNames.LANGUAGE_USED(Locale.forLanguageTag("en").toPlatform()))
        assertEquals(
            "article_opens.9cefae211bd0f75208e5edd1b86ef23f",
            UserCounterNames.ARTICLE_OPEN("https://www.example.com/path".toUriOrNull()!!)
        )
    }
}
