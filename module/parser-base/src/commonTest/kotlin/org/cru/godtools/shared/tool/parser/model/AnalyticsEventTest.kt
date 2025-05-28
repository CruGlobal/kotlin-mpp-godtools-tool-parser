package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyticsEventTest {
    @Test
    fun testIdFallback() {
        assertEquals("action", AnalyticsEvent(action = "action").id)
        assertEquals("id", AnalyticsEvent(action = "action", id = "id").id)
    }
}
