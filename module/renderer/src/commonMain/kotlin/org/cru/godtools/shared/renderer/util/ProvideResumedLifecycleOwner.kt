package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner

/**
 * Provide a [LocalLifecycleOwner] to [content] that is capped at STARTED unless [resumed] is true, so
 * lifecycle-aware effects within [content] only run while it is the "current" child.
 */
@Composable
internal fun ProvideResumedLifecycleOwner(resumed: Boolean, content: @Composable () -> Unit) {
    val lifecycleOwner = rememberLifecycleOwner(
        maxLifecycle = if (resumed) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
    )
    CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner, content = content)
}
