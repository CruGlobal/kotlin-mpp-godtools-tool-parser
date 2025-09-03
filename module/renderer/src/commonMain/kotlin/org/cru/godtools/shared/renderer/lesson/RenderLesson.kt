package org.cru.godtools.shared.renderer.lesson

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.ccci.gto.android.common.androidx.lifecycle.ConstrainedStateLifecycleOwner
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ContentEventListener
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage

internal const val TestTagLessonPager = "LessonPager"
internal val LessonPagerState = SemanticsPropertyKey<PagerState>(
    name = "LessonPagerState",
    mergePolicy = { parentValue, _ ->
        // Never merge TestTags, to avoid leaking internal test tags to parents.
        parentValue
    }
)

@Composable
fun RenderLesson(manifest: Manifest, modifier: Modifier = Modifier, state: State = remember { State() }) {
    if (manifest.type != Manifest.Type.LESSON) return

    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val allPages by rememberUpdatedState(manifest.pages.filterIsInstance<LessonPage>())
    val visiblePages = rememberSaveable { mutableStateSetOf<String>() }
    val pages by remember { derivedStateOf { allPages.filter { it.id in visiblePages || !it.isHidden } } }

    val pagerState = rememberPagerState(0) { pages.size }

    // re-hide hidden pages when they are not the current page
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .map { pages[pagerState.currentPage].id }
            .collect { pageId -> visiblePages.removeAll { it != pageId } }
    }

    // handle page listeners
    ContentEventListener(state) { eventId ->
        val page = allPages.firstOrNull { eventId in it.listeners } ?: return@ContentEventListener
        visiblePages += page.id
        val index = pages.indexOf(page).takeUnless { it == -1 } ?: return@ContentEventListener
        pagerState.animateScrollToPage(index)
    }

    Scaffold(modifier = modifier) { paddingValues ->
        HorizontalPager(
            pagerState,
            beyondViewportPageCount = 1,
            key = { pages[it].id },
            modifier = Modifier
                .testTag(TestTagLessonPager)
                .semantics { this[LessonPagerState] = pagerState },
        ) { i ->
            val isCurrentPage by remember { derivedStateOf { i == pagerState.currentPage } }
            val lifecycleOwner = remember(lifecycleOwner) { ConstrainedStateLifecycleOwner(lifecycleOwner) }
                .apply { maxState = if (isCurrentPage) Lifecycle.State.RESUMED else Lifecycle.State.STARTED }

            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                RenderLessonPage(pages[i], state = state) { event ->
                    when (event) {
                        LessonPageEvent.NextPage -> coroutineScope.launch { pagerState.animateScrollToPage(i + 1) }
                        LessonPageEvent.PreviousPage -> coroutineScope.launch { pagerState.animateScrollToPage(i - 1) }
                    }
                }
            }
        }
    }
}
