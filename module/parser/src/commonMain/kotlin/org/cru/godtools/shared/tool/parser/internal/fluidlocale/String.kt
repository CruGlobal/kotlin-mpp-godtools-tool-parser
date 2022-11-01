package org.cru.godtools.shared.tool.parser.internal.fluidlocale

import io.fluidsonic.locale.Locale
import org.ccci.gto.support.fluidsonic.locale.toPlatform

internal fun String.toLocaleOrNull() = Locale.forLanguageTagOrNull(this)?.toPlatform()
