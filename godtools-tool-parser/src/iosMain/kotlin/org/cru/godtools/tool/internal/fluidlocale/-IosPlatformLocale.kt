package org.cru.godtools.tool.internal.fluidlocale

import io.fluidsonic.locale.Locale
import io.fluidsonic.locale.toCommon
import io.fluidsonic.locale.toPlatform
import platform.Foundation.NSLocale

actual typealias PlatformLocale = NSLocale
internal actual fun Locale.toPlatform() = toPlatform()
internal actual fun PlatformLocale.toCommon() = toCommon()
