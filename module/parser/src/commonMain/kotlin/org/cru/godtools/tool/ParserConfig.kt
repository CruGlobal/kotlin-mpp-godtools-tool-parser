package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType

const val FEATURE_ANIMATION = "animation"
const val FEATURE_CONTENT_CARD = "content_card"
const val FEATURE_FLOW = "flow"
const val FEATURE_MULTISELECT = "multiselect"

expect object ParserConfig {
    var supportedDeviceTypes: Set<DeviceType>
    var supportedFeatures: Set<String>
}

object SimpleParserConfig {
    var supportedDeviceTypes = DEFAULT_SUPPORTED_DEVICE_TYPES
    var supportedFeatures = emptySet<String>()
}

internal expect val DEFAULT_SUPPORTED_DEVICE_TYPES: Set<DeviceType>
