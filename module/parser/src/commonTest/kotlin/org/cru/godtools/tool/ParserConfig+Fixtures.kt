package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType

internal fun ParserConfig.withDeviceType(deviceType: DeviceType) = copy(deviceType = deviceType, appVersion = null)
