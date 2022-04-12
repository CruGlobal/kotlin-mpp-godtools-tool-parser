package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType

const val FEATURE_ANIMATION = "animation"
const val FEATURE_CONTENT_CARD = "content_card"
const val FEATURE_FLOW = "flow"
const val FEATURE_MULTISELECT = "multiselect"

open class ParserConfig(
    open val supportedFeatures: Set<String> = emptySet(),
    open val supportedDeviceTypes: Set<DeviceType> = DEFAULT_SUPPORTED_DEVICE_TYPES
)

expect object LegacyParserConfig : ParserConfig {
    override var supportedDeviceTypes: Set<DeviceType>
    override var supportedFeatures: Set<String>
}

object SimpleParserConfig : ParserConfig() {
    override var supportedDeviceTypes = DEFAULT_SUPPORTED_DEVICE_TYPES
    override var supportedFeatures = emptySet<String>()
}

internal expect val DEFAULT_SUPPORTED_DEVICE_TYPES: Set<DeviceType>
