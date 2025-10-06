package org.cru.godtools.shared.renderer.lesson

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage

@Composable
fun rememberLessonPagerState(manifest: Manifest, initialPage: Int = 0) =
    rememberSaveable(saver = LessonPagerState.Saver) { LessonPagerState(manifest, initialPage) }
        .apply { updateManifest(manifest) }

@Stable
class LessonPagerState private constructor(visiblePages: Collection<String>, pagerState: SaveablePagerState?) {
    constructor(manifest: Manifest? = null, currentPage: Int = 0) : this(
        visiblePages = emptySet(),
        pagerState = SaveablePagerState(currentPage, 0f) { currentPage + 1 },
    ) {
        if (manifest != null) updateManifest(manifest) else updatePages(emptyList())
    }

    internal var allPages: ImmutableList<LessonPage> by mutableStateOf(persistentListOf())
    internal val visiblePages = mutableStateSetOf(*visiblePages.toTypedArray())
    private val pagesState = derivedStateOf {
        allPages.filter { it.id in this.visiblePages || !it.isHidden }.toImmutableList()
    }

    val pages by pagesState
    private val _pagerState = pagerState ?: SaveablePagerState(0, 0f) { pages.size }
    val pagerState: PagerState get() = _pagerState

    fun updateManifest(manifest: Manifest) = updatePages(manifest.pages.filterIsInstance<LessonPage>())
    fun updatePages(pages: List<LessonPage>) {
        allPages = pages.toImmutableList()
        _pagerState.pageCountState.value = { this.pages.size }
    }

    companion object {
        val Saver = listSaver(
            save = {
                listOf(
                    ArrayList(it.visiblePages),
                    with(SaveablePagerState.Saver) { save(it._pagerState) },
                )
            },
            restore = {
                @Suppress("UNCHECKED_CAST")
                LessonPagerState(
                    visiblePages = it[0] as List<String>,
                    pagerState = SaveablePagerState.Saver.restore(it[1] as List<Any>),
                )
            },
        )
    }
}

private class SaveablePagerState(
    currentPage: Int,
    currentPageOffsetFraction: Float,
    updatedPageCount: () -> Int,
) : PagerState(currentPage, currentPageOffsetFraction) {
    var pageCountState = mutableStateOf(updatedPageCount)
    override val pageCount: Int
        get() = pageCountState.value.invoke()

    companion object Companion {
        val Saver = listSaver(
            save = {
                listOf(
                    it.currentPage,
                    it.currentPageOffsetFraction,
                    it.pageCount,
                )
            },
            restore = {
                SaveablePagerState(
                    currentPage = it[0] as Int,
                    currentPageOffsetFraction = it[1] as Float,
                    updatedPageCount = { it[2] as Int },
                )
            },
        )
    }
}
