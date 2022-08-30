package org.cru.godtools.tool

import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.DeviceType
import org.cru.godtools.tool.model.Version
import org.cru.godtools.tool.model.Version.Companion.toVersion

const val FEATURE_ANIMATION = "animation"
const val FEATURE_CONTENT_CARD = "content_card"
const val FEATURE_FLOW = "flow"
const val FEATURE_MULTISELECT = "multiselect"

data class ParserConfig @VisibleForTesting internal constructor(
    internal val deviceType: DeviceType = DeviceType.UNKNOWN,
    internal val appVersion: Version? = null,
    private val supportedFeatures: Set<String> = emptySet(),
    internal val supportedDeviceTypes: Set<DeviceType> = DEFAULT_SUPPORTED_DEVICE_TYPES,
    internal val parsePages: Boolean = true,
    internal val parseTips: Boolean = true
) {
    constructor() : this(supportedFeatures = emptySet())

    fun withAppVersion(deviceType: DeviceType, version: String?) =
        copy(deviceType = deviceType, appVersion = version?.toVersion())
    fun withSupportedDeviceTypes(types: Set<DeviceType>) = copy(supportedDeviceTypes = types)
    fun withSupportedFeatures(features: Set<String>) = copy(supportedFeatures = features)
    fun withParseRelated(enabled: Boolean) = copy(parsePages = enabled, parseTips = enabled)
    fun withParsePages(enabled: Boolean) = copy(parsePages = enabled)
    fun withParseTips(enabled: Boolean) = copy(parseTips = enabled)

    internal fun supportsFeature(feature: String) = feature in supportedFeatures
}

internal expect val DEFAULT_SUPPORTED_DEVICE_TYPES: Set<DeviceType>
