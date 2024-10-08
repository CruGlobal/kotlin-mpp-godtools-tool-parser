package org.cru.godtools.shared.common.model

import android.net.Uri as AndroidUri

// TODO: switch to the Android Uri if Kotlin ever supports varying types between expect & actual
actual typealias Uri = String
actual val Uri.scheme get() = AndroidUri.parse(this).scheme

actual fun String.toUriOrNull(): Uri? = this
