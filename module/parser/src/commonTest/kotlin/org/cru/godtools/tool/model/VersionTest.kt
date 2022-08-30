package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.Version.Companion.toVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VersionTest {
    @Test
    fun testCompareTo() {
        assertTrue("1.0".toVersion() >= "1".toVersion())
        assertTrue("1.0".toVersion() <= "1".toVersion())

        assertTrue("1.1".toVersion() > "1".toVersion())
        assertTrue("1".toVersion() < "1.1".toVersion())
        assertTrue("1.2".toVersion() > "1.1".toVersion())
        assertTrue("1.1.1".toVersion() > "1.0.20".toVersion())
    }

    @Test
    fun testEquals() {
        assertEquals("1.0".toVersion(), "1.0".toVersion())
        assertEquals("1".toVersion(), "1.0".toVersion())
    }
}
