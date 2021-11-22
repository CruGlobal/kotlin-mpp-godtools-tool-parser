package org.cru.godtools.tool.model

import android.net.Uri as AndroidUri

// HACK: Suppress expect/actual errors as workaround for differences between Android Uri and iOS NSURL
//       see: https://discuss.kotlinlang.org/t/feature-request-typealias-for-expected-types/20054/4
@Suppress("ACTUAL_WITHOUT_EXPECT")
actual typealias Uri = AndroidUri
internal actual inline val Uri.scheme get() = scheme

internal actual fun String?.toUriOrNull() = this?.let { AndroidUri.parse(this) }
