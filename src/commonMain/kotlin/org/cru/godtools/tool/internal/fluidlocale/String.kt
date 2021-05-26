package org.cru.godtools.tool.internal.fluidlocale

import io.fluidsonic.locale.Locale

internal fun String.toLocaleOrNull() = Locale.forLanguageTagOrNull(this)?.toPlatform()
