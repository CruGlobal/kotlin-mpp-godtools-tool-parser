package org.cru.godtools.shared.tool.parser.internal.fluidlocale

import io.fluidsonic.locale.Locale

expect class PlatformLocale
internal expect fun PlatformLocale.toCommon(): Locale
internal expect fun Locale.toPlatform(): PlatformLocale
