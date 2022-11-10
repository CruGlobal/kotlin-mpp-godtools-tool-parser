package org.cru.godtools.shared.tool.parser.util

import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.common.model.toUriOrNull
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
        assertEquals(
            "godtools://org.cru.godtools/dashboard/lessons",
            "godtools://org.cru.godtools/dashboard/lessons".toAbsoluteUriOrNull().toString()
        )
    }

    @Test
    fun testScheme() {
        assertEquals("http", "http://example.com".toUriOrNull()!!.scheme)
        assertEquals("https", "https://example.com".toUriOrNull()!!.scheme)
        assertEquals("mailto", "mailto:user@example.com".toUriOrNull()!!.scheme)
        assertEquals("godtools", "godtools://org.cru.godtools/dashboard/lessons".toUriOrNull()!!.scheme)
        assertNull("invalid-uri".toUriOrNull()!!.scheme)
    }

    @Test
    fun testIsHttpUrl() {
        assertTrue("http://example.com".toUriOrNull()!!.isHttpUrl)
        assertTrue("https://example.com".toUriOrNull()!!.isHttpUrl)
        assertFalse("mailto:user@example.com".toUriOrNull()!!.isHttpUrl)
        assertFalse("godtools://org.cru.godtools/dashboard/lessons".toUriOrNull()!!.isHttpUrl)
        assertFalse("invalid-uri".toUriOrNull()!!.isHttpUrl)
    }
}
