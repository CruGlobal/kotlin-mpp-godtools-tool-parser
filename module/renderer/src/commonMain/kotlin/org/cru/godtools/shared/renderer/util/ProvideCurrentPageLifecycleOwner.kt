package org.cru.godtools.shared.renderer.util

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner

/**
 * Provide a [LocalLifecycleOwner] to [content] that is only resumed while [page] is the settled page of [pagerState].
 * Pages that are composed but not settled (e.g. pre-composed via beyondViewportPageCount, or crossed transiently
 * mid-swipe) are capped at STARTED, so lifecycle-aware effects within a page only run once the pager settles on it.
 */
@Composable
internal fun ProvideCurrentPageLifecycleOwner(pagerState: PagerState, page: Int, content: @Composable () -> Unit) {
    val isSettledPage by remember(pagerState, page) { derivedStateOf { page == pagerState.settledPage } }
    val lifecycleOwner = rememberLifecycleOwner(
        maxLifecycle = if (isSettledPage) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
    )

    CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner, content = content)
}
