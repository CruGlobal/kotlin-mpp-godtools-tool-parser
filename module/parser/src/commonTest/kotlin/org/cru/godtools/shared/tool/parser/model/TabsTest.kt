package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger

@RunOnAndroidWith(AndroidJUnit4::class)
class TabsTest : UsesResources() {
    @Test
    fun testParseTabsEmpty() = runTest {
        val tabs = Tabs(Manifest(), getTestXmlParser("tabs_empty.xml"))
        assertEquals(0, tabs.tabs.size)
    }

    @Test
    fun testParseTabsSingle() = runTest {
        val tab = Tabs(Manifest(), getTestXmlParser("tabs_single.xml")).tabs.single()
        assertEquals(0, tab.position)
        assertEquals("Tab 1", tab.label!!.text)
        assertEquals("event1", tab.listeners.single().name)
        assertEquals(1, tab.analyticsEvents.size)
        assertEquals("test", tab.analyticsEvents.single().action)
        assertEquals(2, tab.content.size)
        assertIs<Image>(tab.content[0])
        assertIs<Paragraph>(tab.content[1])
    }

    @Test
    fun testParseTabsMultiple() = runTest {
        val tabs = Tabs(Manifest(), getTestXmlParser("tabs_multiple.xml"))
        assertEquals(2, tabs.tabs.size)
        assertEquals(0, tabs.tabs[0].position)
        assertEquals(1, tabs.tabs[1].position)
    }

    @Test
    fun testParseTabsIgnoredContent() = runTest {
        val manifest = Manifest(ParserConfig().withAppVersion(DeviceType.IOS, null))
        val tab = Tabs(manifest, getTestXmlParser("tabs_ignored_content.xml")).tabs.single()
        assertEquals(1, tab.content.size)
        assertIs<Paragraph>(tab.content[0])
    }

    @Test
    fun testTabGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val tab = Tabs.Tab(analyticsEvents = listOf(defaultEvent, clickedEvent, visibleEvent))

        assertEquals(listOf(defaultEvent, clickedEvent), tab.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { tab.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { tab.getAnalyticsEvents(Trigger.VISIBLE) }
    }
}
