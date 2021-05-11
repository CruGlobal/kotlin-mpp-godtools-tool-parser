package org.cru.godtools.tool.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

private val ID1 = EventId("followup", "seND")

class EventTest {
    @Test
    fun verifyHashCode() {
        assertEquals(EventId.FOLLOWUP.hashCode(), ID1.hashCode())
    }

    @Test
    fun verifyEquals() {
        assertEquals(EventId.FOLLOWUP, ID1)
        assertNotEquals(EventId.FOLLOWUP, EventId("followup", "invalid"))
        assertNotEquals(EventId.FOLLOWUP, EventId(null, "send"))
    }

    @Test
    fun verifyParse() {
        val events = EventId.parse("EVent1 eveNT2 evENt1 followup:send")
        assertEquals(4, events.size)
        assertEquals(EventId(name = "event1"), events[0])
        assertEquals(EventId(name = "event2"), events[1])
        assertEquals(EventId(name = "event1"), events[2])
        assertEquals(EventId.FOLLOWUP, events[3])
    }

    @Test
    fun verifyParseEmpty() {
        assertEquals(0, EventId.parse("").size)
        assertEquals(0, EventId.parse(null).size)
    }
}
