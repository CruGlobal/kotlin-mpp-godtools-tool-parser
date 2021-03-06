package org.cru.godtools.tool.model

import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR

private const val XML_DEVICE_TYPE_ANDROID = "android"
private const val XML_DEVICE_TYPE_IOS = "ios"
private const val XML_DEVICE_TYPE_MOBILE = "mobile"
private const val XML_DEVICE_TYPE_WEB = "web"

enum class DeviceType {
    ANDROID, IOS, MOBILE, WEB, UNKNOWN;

    internal companion object {
        internal val ALL = values().toSet()
        internal val SUPPORTED get() = ParserConfig.supportedDeviceTypes

        private fun String.toDeviceType() = when (this) {
            XML_DEVICE_TYPE_ANDROID -> ANDROID
            XML_DEVICE_TYPE_IOS -> IOS
            XML_DEVICE_TYPE_MOBILE -> MOBILE
            XML_DEVICE_TYPE_WEB -> WEB
            else -> UNKNOWN
        }

        internal fun String.toDeviceTypes() =
            REGEX_SEQUENCE_SEPARATOR.split(this).mapTo(mutableSetOf()) { it.toDeviceType() }
    }
}
