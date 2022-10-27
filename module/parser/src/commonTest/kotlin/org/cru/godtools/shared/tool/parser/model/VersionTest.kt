package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.model.Version.Companion.toVersion
import kotlin.random.Random
import kotlin.random.nextUInt
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

    @Test
    fun testMin() {
        assertEquals(Version.MIN, "0".toVersion())
        assertEquals(Version.MIN, "0.0".toVersion())
        assertTrue("0.0.0.0.0.1".toVersion() > Version.MIN)
    }

    @Test
    fun testMax() {
        assertTrue(Version.MAX > "${Random.nextUInt()}".toVersion())
        assertTrue(Version.MAX >= "${UInt.MAX_VALUE}.${UInt.MAX_VALUE}.${UInt.MAX_VALUE}".toVersion())
    }
}
