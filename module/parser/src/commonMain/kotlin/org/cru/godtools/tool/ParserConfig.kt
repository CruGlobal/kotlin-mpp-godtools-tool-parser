package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType

const val FEATURE_ANIMATION = "animation"
const val FEATURE_CONTENT_CARD = "content_card"
const val FEATURE_FLOW = "flow"
const val FEATURE_MULTISELECT = "multiselect"

class ParserConfig(
    val supportedFeatures: Set<String> = emptySet(),
    val supportedDeviceTypes: Set<DeviceType> = DEFAULT_SUPPORTED_DEVICE_TYPES
)

internal expect val DEFAULT_SUPPORTED_DEVICE_TYPES: Set<DeviceType>
