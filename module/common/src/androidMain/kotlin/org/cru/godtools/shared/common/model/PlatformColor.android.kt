package org.cru.godtools.shared.common.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor

// HACK: Suppress expect/actual errors as workaround for differences between Jetpack Compose Color and iOS UIColor
//       see: https://discuss.kotlinlang.org/t/feature-request-typealias-for-expected-types/20054/4
@Suppress("ACTUAL_WITHOUT_EXPECT")
actual typealias PlatformColor = androidx.compose.ui.graphics.Color

actual fun Color.toPlatformColor() = toComposeColor()
