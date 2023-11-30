package org.cru.godtools.shared.tool.parser

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.cru.godtools.shared.tool.parser.model.DEFAULT
import org.cru.godtools.shared.tool.parser.model.DeviceType
import org.cru.godtools.shared.tool.parser.model.Version
import org.cru.godtools.shared.tool.parser.model.Version.Companion.toVersion

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
data class ParserConfig private constructor(
    internal val deviceType: DeviceType = DeviceType.DEFAULT,
    internal val appVersion: Version? = null,
    private val supportedFeatures: Set<String> = emptySet(),
    internal val parsePages: Boolean = true,
    internal val parseTips: Boolean = true,
    internal val legacyWebImageResources: Boolean = false,
    internal val parserDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    @JsName("createParserConfig")
    constructor() : this(supportedFeatures = emptySet())

    companion object {
        const val FEATURE_ANIMATION = "animation"
        const val FEATURE_CONTENT_CARD = "content_card"
        const val FEATURE_FLOW = "flow"
        const val FEATURE_MULTISELECT = "multiselect"
        internal const val FEATURE_REQUIRED_VERSIONS = "required-versions"
    }

    @JsExport.Ignore
    fun withAppVersion(deviceType: DeviceType = DeviceType.DEFAULT, version: String?) =
        copy(deviceType = deviceType, appVersion = version?.toVersion())
    @HiddenFromObjC
    fun withSupportedFeatures(vararg feature: String) = copy(supportedFeatures = feature.toSet())
    @JsExport.Ignore
    fun withSupportedFeatures(features: Set<String>) = copy(supportedFeatures = features)
    fun withParseRelated(enabled: Boolean) = copy(parsePages = enabled, parseTips = enabled)
    fun withParsePages(enabled: Boolean) = copy(parsePages = enabled)
    fun withParseTips(enabled: Boolean) = copy(parseTips = enabled)
    @HiddenFromObjC
    @Deprecated("Since v0.9.0, This setting is meant to workaround a knowgod.com image resource issue")
    fun withLegacyWebImageResources(enabled: Boolean) = copy(legacyWebImageResources = enabled)

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
