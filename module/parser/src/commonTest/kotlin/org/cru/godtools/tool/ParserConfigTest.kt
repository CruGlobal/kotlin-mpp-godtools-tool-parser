package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
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
}
