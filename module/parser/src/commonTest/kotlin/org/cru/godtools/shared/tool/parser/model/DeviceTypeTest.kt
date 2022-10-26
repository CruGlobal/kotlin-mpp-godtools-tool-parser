package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.model.DeviceType.ANDROID
import org.cru.godtools.shared.tool.parser.model.DeviceType.Companion.toDeviceTypes
import org.cru.godtools.shared.tool.parser.model.DeviceType.IOS
import org.cru.godtools.shared.tool.parser.model.DeviceType.MOBILE
import org.cru.godtools.shared.tool.parser.model.DeviceType.UNKNOWN
import org.cru.godtools.shared.tool.parser.model.DeviceType.WEB
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceTypeTest {
    @Test
    fun verifyToDeviceTypes() {
        assertEquals(setOf(ANDROID), "android".toDeviceTypes())
        assertEquals(setOf(IOS), "ios".toDeviceTypes())
        assertEquals(setOf(MOBILE), "mobile".toDeviceTypes())
        assertEquals(setOf(WEB), "web".toDeviceTypes())
        assertEquals(setOf(UNKNOWN), "hjasdf".toDeviceTypes())

        assertEquals(setOf(UNKNOWN), "aljksdf ajklsdfa awe".toDeviceTypes())
        assertEquals(setOf(MOBILE, UNKNOWN), "mobile ajklsdfa awe".toDeviceTypes())
        assertEquals(setOf(ANDROID, MOBILE, UNKNOWN), "android mobile aw".toDeviceTypes())
    }
}
