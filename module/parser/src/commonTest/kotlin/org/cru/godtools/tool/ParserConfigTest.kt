package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType
import org.cru.godtools.tool.model.Version.Companion.toVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParserConfigTest {
    @Test
    fun testWithAppVersion() {
        val orig = ParserConfig(deviceType = DeviceType.UNKNOWN)
        assertEquals(DeviceType.UNKNOWN, orig.deviceType)
        assertNull(orig.appVersion)

        val updated = orig.withAppVersion(DeviceType.ANDROID, "10.3.4")
        assertEquals(DeviceType.ANDROID, updated.deviceType)
        assertEquals("10.3.4".toVersion(), updated.appVersion)
    }

    @Test
    fun testWithSupportedDeviceTypes() {
        val orig = ParserConfig(supportedDeviceTypes = emptySet())
        val updated = orig.withSupportedDeviceTypes(setOf(DeviceType.ANDROID))

        assertTrue(orig.supportedDeviceTypes.isEmpty())
        assertEquals(setOf(DeviceType.ANDROID), updated.supportedDeviceTypes)
    }

    @Test
    fun testWithSupportedFeatures() {
        val orig = ParserConfig(supportedFeatures = emptySet())
        val updated = orig.withSupportedFeatures(setOf("test"))

        assertFalse(orig.supportsFeature("test"))
        assertTrue(updated.supportsFeature("test"))
    }

    @Test
    fun testWithParseRelated() {
        val orig = ParserConfig(parsePages = true, parseTips = true)
        val updated = orig.withParseRelated(false)

        assertTrue(orig.parsePages)
        assertTrue(orig.parseTips)
        assertFalse(updated.parsePages)
        assertFalse(updated.parseTips)
    }

    @Test
    fun testWithParsePages() {
        val orig = ParserConfig(parsePages = true)
        val updated = orig.withParsePages(false)

        assertTrue(orig.parsePages)
        assertFalse(updated.parsePages)
    }

    @Test
    fun testWithParseTips() {
        val orig = ParserConfig(parseTips = true)
        val updated = orig.withParseTips(false)

        assertTrue(orig.parseTips)
        assertFalse(updated.parseTips)
    }

    @Test
    fun testSupportsFeatureRequiredVersions() {
        val config = ParserConfig()
        assertFalse(config.supportsFeature(FEATURE_REQUIRED_VERSIONS))
        assertTrue(config.withAppVersion(DeviceType.values().random(), "1").supportsFeature(FEATURE_REQUIRED_VERSIONS))
    }
}
