package org.cru.godtools.tool

import org.cru.godtools.tool.model.DeviceType

actual typealias ParserConfig = SimpleParserConfig

internal actual val DEFAULT_SUPPORTED_DEVICE_TYPES = setOf(DeviceType.WEB)
