package org.cru.godtools.shared.common.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith

@RunOnAndroidWith(AndroidJUnit4::class)
class UriTest {
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
