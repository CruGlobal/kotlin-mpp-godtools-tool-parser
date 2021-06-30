package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual object ParserConfig {
    private val _supportedDeviceTypes = AtomicReference(DEFAULT_SUPPORTED_DEVICE_TYPES)
    actual var supportedDeviceTypes: Set<DeviceType>
        get() = _supportedDeviceTypes.value
        set(value) {
            _supportedDeviceTypes.value = if (value.isFrozen) value else value.toSet().freeze()
        }

    private val _supportedFeatures = AtomicReference(emptySet<String>())
    actual var supportedFeatures: Set<String>
        get() = _supportedFeatures.value
        set(value) {
            _supportedFeatures.value = if (value.isFrozen) value else value.toSet().freeze()
        }
}

@SharedImmutable
internal actual val DEFAULT_SUPPORTED_DEVICE_TYPES = setOf(DeviceType.IOS, DeviceType.MOBILE)
