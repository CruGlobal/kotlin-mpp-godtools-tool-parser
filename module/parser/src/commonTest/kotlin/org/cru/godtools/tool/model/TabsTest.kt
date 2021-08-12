package org.cru.godtools.tool.model

import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class TabsTest : UsesResources() {
    @BeforeTest
    fun setupConfig() {
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.MOBILE)
    }

    @Test
    fun testParseTabsEmpty() = runBlockingTest {
        val tabs = Tabs(Manifest(), getTestXmlParser("tabs_empty.xml"))
        assertEquals(0, tabs.tabs.size)
    }

    @Test
    fun testParseTabsSingle() = runBlockingTest {
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
    fun testParseTabsMultiple() = runBlockingTest {
        val tabs = Tabs(Manifest(), getTestXmlParser("tabs_multiple.xml"))
        assertEquals(2, tabs.tabs.size)
        assertEquals(0, tabs.tabs[0].position)
        assertEquals(1, tabs.tabs[1].position)
    }

    @Test
    fun testParseTabsIgnoredContent() = runBlockingTest {
        val tab = Tabs(Manifest(), getTestXmlParser("tabs_ignored_content.xml")).tabs.single()
        assertEquals(1, tab.content.size)
        assertIs<Paragraph>(tab.content[0])
    }
}
