package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.LegacyParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TabsTest : UsesResources() {
    @BeforeTest
    fun setupConfig() {
        LegacyParserConfig.supportedDeviceTypes = setOf(DeviceType.MOBILE)
    }

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
        val tab = Tabs(Manifest(), getTestXmlParser("tabs_ignored_content.xml")).tabs.single()
        assertEquals(1, tab.content.size)
        assertIs<Paragraph>(tab.content[0])
    }

    @Test
    fun testTabGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val selectedEvent = AnalyticsEvent(trigger = Trigger.SELECTED)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val tab = Tabs.Tab(analyticsEvents = listOf(defaultEvent, clickedEvent, selectedEvent, visibleEvent))

        assertEquals(listOf(defaultEvent, clickedEvent), tab.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { tab.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { tab.getAnalyticsEvents(Trigger.SELECTED) }
        assertFailsWith(IllegalStateException::class) { tab.getAnalyticsEvents(Trigger.VISIBLE) }
    }
}
