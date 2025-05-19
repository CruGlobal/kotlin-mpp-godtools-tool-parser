package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals

class EventIdParsingTest {
    @Test
    fun verifyParse() {
        val events = "EVent1 eveNT2 evENt1 followup:send".toEventIds()
        assertEquals(4, events.size)
        assertEquals(EventId(name = "event1"), events[0])
        assertEquals(EventId(name = "event2"), events[1])
        assertEquals(EventId(name = "event1"), events[2])
        assertEquals(EventId.FOLLOWUP, events[3])
    }

    @Test
    fun verifyParseEmpty() {
        assertEquals(emptyList(), "".toEventIds())
        assertEquals(emptyList(), " ".toEventIds())
    }
}
