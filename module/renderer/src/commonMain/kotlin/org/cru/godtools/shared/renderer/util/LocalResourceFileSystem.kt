@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.staticCompositionLocalOf
import okio.FileSystem

internal val LocalResourceFileSystem = staticCompositionLocalOf<FileSystem> {
    error("CompositionLocal LocalResourceFileSystem not present")
}
