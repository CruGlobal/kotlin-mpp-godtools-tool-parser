package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class ResourceTest {
    @Test
    fun testEquals() {
        assertEquals(Resource(), Resource())
        assertEquals(Resource(name = "name", localName = "local"), Resource(name = "name", localName = "local"))

        assertNotEquals(Resource(name = "name", localName = "local"), Resource(name = "name2", localName = "local"))
        assertNotEquals(Resource(name = "name", localName = "local"), Resource(name = "name", localName = "local2"))

        assertFalse(Resource().equals(null))
        assertFalse(Resource().equals("resource"))
    }

    @Test
    fun testHashCode() {
        assertEquals(Resource().hashCode(), Resource().hashCode())
        assertEquals(
            Resource(name = "name", localName = "local").hashCode(),
            Resource(name = "name", localName = "local").hashCode(),
        )
    }
}
