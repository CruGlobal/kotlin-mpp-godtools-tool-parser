package org.cru.godtools.shared.common.model

import com.github.ajalt.colormath.Color

expect class PlatformColor

expect fun Color.toPlatformColor(): PlatformColor
