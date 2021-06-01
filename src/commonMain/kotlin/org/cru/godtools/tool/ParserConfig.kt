package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType

expect object ParserConfig {
    var supportedDeviceTypes: Set<DeviceType>
}

object SimpleParserConfig {
    var supportedDeviceTypes: Set<DeviceType> = DEFAULT_SUPPORTED_DEVICE_TYPES
}

internal expect val DEFAULT_SUPPORTED_DEVICE_TYPES: Set<DeviceType>
