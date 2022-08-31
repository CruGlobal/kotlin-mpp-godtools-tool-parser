package org.cru.godtools.tool

import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.DEFAULT
import org.cru.godtools.tool.model.DeviceType
import org.cru.godtools.tool.model.Version
import org.cru.godtools.tool.model.Version.Companion.toVersion

const val FEATURE_ANIMATION = "animation"
const val FEATURE_CONTENT_CARD = "content_card"
const val FEATURE_FLOW = "flow"
const val FEATURE_MULTISELECT = "multiselect"
internal const val FEATURE_REQUIRED_VERSIONS = "required-versions"

data class ParserConfig @VisibleForTesting internal constructor(
    internal val deviceType: DeviceType = DeviceType.DEFAULT,
    internal val appVersion: Version? = null,
    private val supportedFeatures: Set<String> = emptySet(),
    internal val parsePages: Boolean = true,
    internal val parseTips: Boolean = true
) {
    constructor() : this(supportedFeatures = emptySet())

    fun withAppVersion(deviceType: DeviceType, version: String?) =
        copy(deviceType = deviceType, appVersion = version?.toVersion())
    @Deprecated("Since v0.7.0, use withAppVersion(deviceType, version) instead.")
    fun withSupportedDeviceTypes(types: Set<DeviceType>) = withAppVersion(
        deviceType = types.firstOrNull { it == DeviceType.ANDROID || it == DeviceType.IOS } ?: DeviceType.DEFAULT,
        version = null
    )
    fun withSupportedFeatures(features: Set<String>) = copy(supportedFeatures = features)
    fun withParseRelated(enabled: Boolean) = copy(parsePages = enabled, parseTips = enabled)
    fun withParsePages(enabled: Boolean) = copy(parsePages = enabled)
    fun withParseTips(enabled: Boolean) = copy(parseTips = enabled)

    internal fun supportsDeviceType(type: DeviceType) = when (type) {
        deviceType -> true
        DeviceType.MOBILE -> deviceType == DeviceType.ANDROID || deviceType == DeviceType.IOS
        else -> false
    }
    internal fun supportsFeature(feature: String) = when (feature) {
        FEATURE_REQUIRED_VERSIONS -> appVersion != null
        else -> feature in supportedFeatures
    }
}
