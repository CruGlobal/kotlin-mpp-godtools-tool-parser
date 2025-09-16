package org.cru.godtools.shared.renderer.lesson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.ccci.gto.android.common.androidx.lifecycle.ConstrainedStateLifecycleOwner
import org.cru.godtools.shared.renderer.RenderBackground
import org.cru.godtools.shared.renderer.ToolTheme.ProgressBarGapSize
import org.cru.godtools.shared.renderer.ToolTheme.ProgressBarHeight
import org.cru.godtools.shared.renderer.common.ToolLoading
import org.cru.godtools.shared.renderer.common.ToolNotFound
import org.cru.godtools.shared.renderer.common.ToolOffline
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_close
import org.cru.godtools.shared.renderer.util.ContentEventListener
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.lessonNavBarColor
import org.cru.godtools.shared.tool.parser.model.lessonNavBarControlColor
import org.jetbrains.compose.resources.stringResource

internal const val TestTagLessonPager = "LessonPager"

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RenderLesson(state: LessonScreen.UiState, modifier: Modifier = Modifier) {
    val eventSink by rememberUpdatedState(state.eventSink)

    Scaffold(
        topBar = {
            val appBarColor = state.manifest.lessonNavBarColor.toComposeColor()
            val appBarControlColor = state.manifest.lessonNavBarControlColor.toComposeColor()

            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { eventSink(LessonScreen.UiEvent.CloseLesson) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.lesson_accessibility_action_close),
                            tint = appBarControlColor,
                        )
                    }
                },
                title = {
                    if (state is LessonScreen.UiState.Loaded) {
                        LinearProgressIndicator(
                            progress = {
                                val pagerState = state.pagerState
                                when (val pageCount = pagerState.pageCount) {
                                    0 -> 0f
                                    else -> (pagerState.currentPage + 1 + pagerState.currentPageOffsetFraction) /
                                        pageCount
                                }
                            },
                            gapSize = ProgressBarGapSize,
                            color = appBarControlColor,
                            trackColor = appBarControlColor.copy(alpha = appBarControlColor.alpha * 0.24f),
                            drawStopIndicator = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                                .height(ProgressBarHeight)
                        )
                    }
                },
                actions = {
                    // HACK: to center the title, we add a spacer the same size as the navigation icon
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor,
                    navigationIconContentColor = appBarControlColor,
                    titleContentColor = appBarControlColor
                ),
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        when (state) {
            is LessonScreen.UiState.Loaded -> Box {
                RenderBackground(state.manifest.background, Modifier.matchParentSize())
                RenderLessonPager(state, contentInsets = paddingValues)
            }
            is LessonScreen.UiState.Loading -> ToolLoading(
                progress = state.progress,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
            is LessonScreen.UiState.Missing -> ToolNotFound(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
            is LessonScreen.UiState.Offline -> ToolOffline(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun RenderLessonPager(
    state: LessonScreen.UiState.Loaded,
    contentInsets: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val pagerState = state.pagerState

    val allPages by rememberUpdatedState(state.manifest.pages.filterIsInstance<LessonPage>())
    val visiblePages = rememberSaveable { mutableStateSetOf<String>() }
    val pages by remember { derivedStateOf { allPages.filter { it.id in visiblePages || !it.isHidden } } }
    pagerState.pageCountLambda = { pages.size }

    // re-hide hidden pages when they are not the current page
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .mapNotNull { pages.getOrNull(pagerState.currentPage)?.id }
            .collect { pageId -> visiblePages.removeAll { it != pageId } }
    }

    // handle page listeners
    ContentEventListener(state.state) { eventId ->
        val page = allPages.firstOrNull { eventId in it.listeners } ?: return@ContentEventListener
        visiblePages += page.id
        val index = pages.indexOf(page).takeUnless { it == -1 } ?: return@ContentEventListener
        pagerState.animateScrollToPage(index)
    }

    HorizontalPager(
        pagerState,
        beyondViewportPageCount = 1,
        key = { pages[it].id },
        modifier = modifier.testTag(TestTagLessonPager)
    ) { i ->
        val isCurrentPage by remember { derivedStateOf { i == pagerState.currentPage } }
        val lifecycleOwner = remember(lifecycleOwner) { ConstrainedStateLifecycleOwner(lifecycleOwner) }
            .apply { maxState = if (isCurrentPage) Lifecycle.State.RESUMED else Lifecycle.State.STARTED }

        CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
            RenderLessonPage(pages[i], state = state.state, contentInsets = contentInsets) { event ->
                when (event) {
                    LessonPageEvent.NextPage -> coroutineScope.launch { pagerState.animateScrollToPage(i + 1) }
                    LessonPageEvent.PreviousPage -> coroutineScope.launch { pagerState.animateScrollToPage(i - 1) }
                }
            }
        }
    }
}
