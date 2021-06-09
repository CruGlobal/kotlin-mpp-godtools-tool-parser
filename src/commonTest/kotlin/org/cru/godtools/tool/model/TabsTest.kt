package org.cru.godtools.tool.model

import org.cru.godtools.tool.DEFAULT_SUPPORTED_DEVICE_TYPES
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.AfterTest
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

    @AfterTest
    fun resetConfig() {
        ParserConfig.supportedDeviceTypes = DEFAULT_SUPPORTED_DEVICE_TYPES
    }

    @Test
    fun testParseTabs() {
        val tabs = Tabs(Manifest(), getTestXmlParser("tabs.xml"))
        assertEquals(2, tabs.tabs.size)
        assertEquals(0, tabs.tabs[0].position)
        assertEquals(1, tabs.tabs[1].position)
    }

    @Test
    fun testParseTab() {
        val tab = Tabs(Manifest(), getTestXmlParser("tabs_single.xml")).tabs.single()
        assertEquals(0, tab.position)
        assertEquals(1, tab.analyticsEvents.size)
        assertEquals("Tab 1", tab.label!!.text)
        assertEquals(3, tab.content.size)
        assertIs<Image>(tab.content[0])
        assertIs<Paragraph>(tab.content[1])
        assertIs<Tabs>(tab.content[2])
    }

    @Test
    fun testParseTabIgnoredContent() {
        val tab = Tabs(Manifest(), getTestXmlParser("tabs_ignored_content.xml")).tabs.single()
        assertEquals(2, tab.content.size)
        assertIs<Paragraph>(tab.content[0])
        assertIs<Tabs>(tab.content[1])
    }
}
