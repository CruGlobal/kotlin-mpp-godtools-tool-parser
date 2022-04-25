package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParserConfigTest {
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

        assertTrue(orig.supportedFeatures.isEmpty())
        assertEquals(setOf("test"), updated.supportedFeatures)
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
}
