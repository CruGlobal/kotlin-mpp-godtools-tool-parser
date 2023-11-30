package org.cru.godtools.shared.tool.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_REQUIRED_VERSIONS
import org.cru.godtools.shared.tool.parser.model.DeviceType
import org.cru.godtools.shared.tool.parser.model.Version.Companion.toVersion

class ParserConfigTest {
    @Test
    fun testWithAppVersion() {
        val orig = ParserConfig().withDeviceType(DeviceType.UNKNOWN)
        assertEquals(DeviceType.UNKNOWN, orig.deviceType)
        assertNull(orig.appVersion)

        val updated = orig.withAppVersion(DeviceType.ANDROID, "10.3.4")
        assertEquals(DeviceType.ANDROID, updated.deviceType)
        assertEquals("10.3.4".toVersion(), updated.appVersion)
    }

    @Test
    fun testWithSupportedFeatures() {
        val orig = ParserConfig().withSupportedFeatures()
        val updated = orig.withSupportedFeatures("test")

        assertFalse(orig.supportsFeature("test"))
        assertTrue(updated.supportsFeature("test"))
    }

    @Test
    fun testWithParseRelated() {
        val orig = ParserConfig().withParsePages(true).withParseTips(true)
        val updated = orig.withParseRelated(false)

        assertTrue(orig.parsePages)
        assertTrue(orig.parseTips)
        assertFalse(updated.parsePages)
        assertFalse(updated.parseTips)
    }

    @Test
    fun testWithParsePages() {
        val orig = ParserConfig().withParsePages(true)
        val updated = orig.withParsePages(false)

        assertTrue(orig.parsePages)
        assertFalse(updated.parsePages)
    }

    @Test
    fun testWithParseTips() {
        val orig = ParserConfig().withParseTips(true)
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
