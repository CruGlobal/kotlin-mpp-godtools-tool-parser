package org.cru.godtools.shared.common.model

import com.github.ajalt.colormath.Color
import org.cru.godtools.shared.common.internal.colormath.toUIColor
import platform.UIKit.UIColor

@Suppress("CONFLICTING_OVERLOADS")
actual typealias PlatformColor = UIColor

actual fun Color.toPlatformColor() = toUIColor()
