package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class ResourceTest {
    @Test
    fun testEquals() {
        assertEquals(Resource(), Resource())
        assertEquals(Resource(name = "name", src = "local"), Resource(name = "name", src = "local"))

        assertNotEquals(Resource(name = "name", src = "local"), Resource(name = "name2", src = "local"))
        assertNotEquals(Resource(name = "name", src = "local"), Resource(name = "name", src = "local2"))
        assertNotEquals(
            Resource(name = "name", src = "local"),
            Resource(name = "name", src = "local", checksumSha256 = "abc"),
        )
        assertNotEquals(
            Resource(name = "name", src = "local"),
            Resource(name = "name", src = "local", size = 123),
        )

        assertFalse(Resource().equals(null))
        assertFalse(Resource().equals("resource"))
    }

    @Test
    fun testHashCode() {
        assertEquals(Resource().hashCode(), Resource().hashCode())
        assertEquals(
            Resource(name = "name", src = "local").hashCode(),
            Resource(name = "name", src = "local").hashCode(),
        )
        assertEquals(
            Resource(name = "name", src = "local", checksumSha256 = "abc", size = 123).hashCode(),
            Resource(name = "name", src = "local", checksumSha256 = "abc", size = 123).hashCode(),
        )
    }
}
