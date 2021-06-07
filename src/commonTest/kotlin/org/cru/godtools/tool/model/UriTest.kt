package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunOnAndroidWith(AndroidJUnit4::class)
class UriTest {
    @Test
    fun testToAbsoluteUri() {
        assertEquals("https://example.com/path", "https://example.com/path".toAbsoluteUri().toString())
        assertEquals("http://example.com/path", "example.com/path".toAbsoluteUri().toString())
        assertEquals("mailto:someone@example.com", "mailto:someone@example.com".toAbsoluteUri().toString())
    }
}
