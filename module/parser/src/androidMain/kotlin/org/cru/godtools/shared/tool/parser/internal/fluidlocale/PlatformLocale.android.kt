package org.cru.godtools.shared.tool.parser.internal.fluidlocale

import io.fluidsonic.locale.Locale
import io.fluidsonic.locale.toCommon
import io.fluidsonic.locale.toPlatform

actual typealias PlatformLocale = java.util.Locale
internal actual fun Locale.toPlatform() = toPlatform()
internal actual fun PlatformLocale.toCommon() = toCommon()
