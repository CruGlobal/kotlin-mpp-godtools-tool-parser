package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class UriTest {
    @Test
    fun testToAbsoluteUriOrNull() {
        assertEquals("https://example.com/path", "https://example.com/path".toAbsoluteUriOrNull().toString())
        assertEquals("http://example.com/path", "example.com/path".toAbsoluteUriOrNull().toString())
        assertEquals("mailto:someone@example.com", "mailto:someone@example.com".toAbsoluteUriOrNull().toString())
    }

    @Test
    fun testScheme() {
        assertEquals("http", "http://example.com".toUri().scheme)
        assertEquals("https", "https://example.com".toUri().scheme)
        assertEquals("mailto", "mailto:user@example.com".toUri().scheme)
        assertNull("invalid-uri".toUri().scheme)
    }

    @Test
    fun testIsHttpUrl() {
        assertTrue("http://example.com".toUri().isHttpUrl)
        assertTrue("https://example.com".toUri().isHttpUrl)
        assertFalse("mailto:user@example.com".toUri().isHttpUrl)
        assertFalse("invalid-uri".toUri().isHttpUrl)
    }
}
