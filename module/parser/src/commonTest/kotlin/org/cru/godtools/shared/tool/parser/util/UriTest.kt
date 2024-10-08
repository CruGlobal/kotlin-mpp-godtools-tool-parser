package org.cru.godtools.shared.tool.parser.util

import kotlin.test.Test
import kotlin.test.assertEquals
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith

@RunOnAndroidWith(AndroidJUnit4::class)
class UriTest {
    @Test
    fun testToAbsoluteUriOrNull() {
        assertEquals("https://example.com/path", "https://example.com/path".toAbsoluteUriOrNull().toString())
        assertEquals("http://example.com/path", "example.com/path".toAbsoluteUriOrNull().toString())
        assertEquals("mailto:someone@example.com", "mailto:someone@example.com".toAbsoluteUriOrNull().toString())
        assertEquals(
            "godtools://org.cru.godtools/dashboard/lessons",
            "godtools://org.cru.godtools/dashboard/lessons".toAbsoluteUriOrNull().toString()
        )
    }
}
