package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.cru.godtools.shared.tool.parser.expressions.SimpleExpressionContext

class EventIdTest {
    companion object {
        private val ID1 = EventId("followup", "seND")
    }

    // region resolve(ExpressionContext)
    @Test
    fun testResolve() {
        val ctx = SimpleExpressionContext()
        ctx.setVar("selectorState", listOf("a", "b"))
        val events = EventId(EventId.NAMESPACE_STATE, "selectorState").resolve(ctx)
        assertEquals(2, events.size)
        assertEquals(EventId(name = "a"), events[0])
        assertEquals(EventId(name = "b"), events[1])
    }

    @Test
    fun testResolveUnset() {
        assertTrue(EventId(EventId.NAMESPACE_STATE, "missing").resolve(SimpleExpressionContext()).isEmpty())
    }

    @Test
    fun testResolveDifferentNamespace() {
        val ctx = SimpleExpressionContext()
        val event = EventId(name = "event")
        ctx.setVar("event", listOf("a", "b"))
        assertEquals(event, event.resolve(ctx).single())
    }
    // endregion resolve(ExpressionContext)

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
