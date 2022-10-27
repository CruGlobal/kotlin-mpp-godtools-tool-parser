package org.cru.godtools.shared.tool.parser

import org.cru.godtools.shared.tool.parser.model.DEFAULT
import org.cru.godtools.shared.tool.parser.model.DeviceType

// HACK: Kotlin/Native doesn't support optional default args, so we override the method to make the default arg optional
fun ParserConfig.withAppVersion(version: String?) = withAppVersion(deviceType = DeviceType.DEFAULT, version = version)
