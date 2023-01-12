package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.shared.common.internal.colormath.toColormathSRGB
import org.cru.godtools.shared.common.internal.colormath.toUIColor
import platform.UIKit.UIColor

@Suppress("CONFLICTING_OVERLOADS")
actual typealias PlatformColor = UIColor

internal actual fun RGB.toPlatformColor() = toUIColor()
internal actual fun PlatformColor.toRGB() = toColormathSRGB()
