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

@Composable
fun rememberLessonPagerState(manifest: Manifest, initialPage: LessonPage?) =
    rememberSaveable(saver = LessonPagerState.Saver) { LessonPagerState(manifest, initialPage) }
        .apply { updateManifest(manifest) }

@Stable
class LessonPagerState private constructor(revealedPages: Collection<String>, pagerState: SaveablePagerState?) {
    constructor(manifest: Manifest? = null, currentPage: Int = 0) : this(
        revealedPages = emptySet(),
        pagerState = SaveablePagerState(currentPage, 0f) { currentPage + 1 },
    ) {
        if (manifest != null) updateManifest(manifest) else updatePages(emptyList())
    }

    constructor(manifest: Manifest, currentPage: LessonPage?) : this(
        revealedPages = setOfNotNull(currentPage?.id),
        pagerState = createPagerState(manifest, currentPage),
    ) {
        updateManifest(manifest)
    }

    internal var allPages: ImmutableList<LessonPage> by mutableStateOf(persistentListOf())
    internal val revealedPages = mutableStateSetOf(*revealedPages.toTypedArray())
    val pages by derivedStateOf { allPages.filterVisiblePages(this.revealedPages).toImmutableList() }

    val pagerState: PagerState
        field: SaveablePagerState = pagerState ?: SaveablePagerState(0, 0f) { pages.size }
    val settledPage by derivedStateOf { pages.getOrNull(this.pagerState.settledPage) }

    fun updateManifest(manifest: Manifest) = updatePages(manifest.pages.filterIsInstance<LessonPage>())
    fun updatePages(pages: List<LessonPage>) {
        allPages = pages.toImmutableList()
        pagerState.pageCountState.value = { this.pages.size }
    }

    companion object {
        val Saver = listSaver(
            save = {
                listOf(
                    ArrayList(it.revealedPages),
                    with(SaveablePagerState.Saver) { save(it.pagerState) },
                )
            },
            restore = {
                @Suppress("UNCHECKED_CAST")
                LessonPagerState(
                    revealedPages = it[0] as List<String>,
                    pagerState = SaveablePagerState.Saver.restore(it[1] as List<Any>),
                )
            },
        )

        private fun createPagerState(manifest: Manifest, currentPage: LessonPage?): SaveablePagerState {
            val index = when (currentPage) {
                null -> 0

                else -> manifest.pages.filterIsInstance<LessonPage>()
                    .filterVisiblePages(setOf(currentPage.id))
                    .indexOfFirst { it.id == currentPage.id }
                    .coerceAtLeast(0)
            }
            return SaveablePagerState(index, 0f) { index + 1 }
        }

        private fun List<LessonPage>.filterVisiblePages(revealedPages: Set<String>) =
            filter { !it.isHidden || it.id in revealedPages }
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
