package org.cru.godtools.shared.tool.parser.internal.fluidlocale

import io.fluidsonic.locale.Locale

internal fun String.toLocaleOrNull() = Locale.forLanguageTagOrNull(this)?.toPlatform()
