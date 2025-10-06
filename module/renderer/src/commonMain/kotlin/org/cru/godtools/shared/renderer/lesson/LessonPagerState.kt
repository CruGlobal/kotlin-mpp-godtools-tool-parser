package org.cru.godtools.shared.renderer.lesson

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage

@Composable
fun rememberLessonPagerState(manifest: Manifest, initialPage: Int = 0) = remember { LessonPagerState(initialPage) }
    .apply { updateManifest(manifest) }

@Stable
class LessonPagerState(currentPage: Int = 0) {
    internal var allPages: ImmutableList<LessonPage> by mutableStateOf(persistentListOf())
    internal val visiblePages = mutableStateSetOf<String>()
    private val pagesState = derivedStateOf {
        allPages.filter { it.id in visiblePages || !it.isHidden }.toImmutableList()
    }

    val pages by pagesState
    val pagerState = PagerState(currentPage) { pages.size }

    fun updateManifest(manifest: Manifest) = updatePages(manifest.pages.filterIsInstance<LessonPage>())
    fun updatePages(pages: List<LessonPage>) {
        allPages = pages.toImmutableList()
    }
}
