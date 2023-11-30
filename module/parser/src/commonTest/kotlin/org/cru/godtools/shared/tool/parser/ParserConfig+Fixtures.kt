@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.tool.parser

import org.cru.godtools.shared.tool.parser.model.DeviceType

internal fun ParserConfig.withDeviceType(deviceType: DeviceType) = copy(deviceType = deviceType, appVersion = null)
