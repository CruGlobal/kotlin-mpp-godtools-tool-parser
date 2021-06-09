package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class LinkTest : UsesResources() {
    @Test
    fun testParseLink() {
        val link = Link(Manifest(), getTestXmlParser("link.xml"))
        assertEquals("Test", link.text!!.text)
        assertEquals(2, link.events.size)
        assertEquals("event1", link.events[0].name)
        assertEquals("event2", link.events[1].name)
        assertEquals(1, link.analyticsEvents.size)
        assertEquals("test", assertIs<AnalyticsEvent>(link.analyticsEvents.single()).action)
    }
}
