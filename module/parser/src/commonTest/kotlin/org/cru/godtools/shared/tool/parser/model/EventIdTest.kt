package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class EventIdTest {
    companion object {
        private val ID1 = EventId("followup", "seND")
    }

    @Test
    fun verifyHashCode() {
        assertEquals(EventId.FOLLOWUP.hashCode(), ID1.hashCode())
        assertEquals(EventId(name = "ABC"), EventId(name = "abc"))
    }

    @Test
    fun verifyEquals() {
        assertEquals(EventId.FOLLOWUP, ID1)
        assertNotEquals(EventId.FOLLOWUP, EventId("followup", "invalid"))
        assertNotEquals(EventId.FOLLOWUP, EventId(null, "send"))
        assertFalse(EventId.FOLLOWUP == Any())
    }

    @Test
    fun verifyToString() {
        assertEquals("followup:send", EventId.FOLLOWUP.toString())
        assertEquals("abc", EventId(name = "abc").toString())
    }
}
