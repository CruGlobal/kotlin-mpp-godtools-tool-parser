package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType

const val FEATURE_ANIMATION = "animation"
const val FEATURE_CONTENT_CARD = "content_card"
const val FEATURE_FLOW = "flow"
const val FEATURE_MULTISELECT = "multiselect"

data class ParserConfig(
    internal val supportedFeatures: Set<String> = emptySet(),
    internal val supportedDeviceTypes: Set<DeviceType> = DEFAULT_SUPPORTED_DEVICE_TYPES,
    internal val parsePages: Boolean = true,
    internal val parseTips: Boolean = true
) {
    constructor() : this(supportedFeatures = emptySet())

    fun withSupportedDeviceTypes(types: Set<DeviceType>) = copy(supportedDeviceTypes = types)
    fun withSupportedFeatures(features: Set<String>) = copy(supportedFeatures = features)
    fun withParsePages(enabled: Boolean) = copy(parsePages = enabled)
    fun withParseTips(enabled: Boolean) = copy(parseTips = enabled)
}

internal expect val DEFAULT_SUPPORTED_DEVICE_TYPES: Set<DeviceType>
