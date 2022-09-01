package org.cru.godtools.tool

import org.cru.godtools.tool.model.DEFAULT
import org.cru.godtools.tool.model.DeviceType

// HACK: Kotlin/Native doesn't support optional default args, so we override the method to make the default arg optional
fun ParserConfig.withAppVersion(version: String?) = withAppVersion(deviceType = DeviceType.DEFAULT, version = version)
