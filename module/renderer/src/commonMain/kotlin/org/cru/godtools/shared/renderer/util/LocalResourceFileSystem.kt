@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import okio.FileSystem

internal val LocalResourceFileSystem: ProvidableCompositionLocal<FileSystem> =
    staticCompositionLocalOf { error("CompositionLocal LocalResourceFileSystem not present") }
