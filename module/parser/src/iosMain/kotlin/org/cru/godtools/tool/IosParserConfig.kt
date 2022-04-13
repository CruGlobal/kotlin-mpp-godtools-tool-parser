package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
internal actual val DEFAULT_SUPPORTED_DEVICE_TYPES = setOf(DeviceType.IOS, DeviceType.MOBILE)
