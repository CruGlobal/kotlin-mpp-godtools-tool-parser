package org.cru.godtools.shared.renderer.lesson

import androidx.annotation.FloatRange
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun rememberLessonPagerState(
    initialPage: Int = 0,
    @FloatRange(from = -0.5, to = 0.5) initialPageOffsetFraction: Float = 0f,
) = rememberSaveable(saver = LessonPagerState.Saver) {
    LessonPagerState(initialPage, initialPageOffsetFraction)
}

class LessonPagerState(
    currentPage: Int = 0,
    @FloatRange(from = -0.5, to = 0.5) currentPageOffsetFraction: Float = 0f,
    pageCountLambda: () -> Int = { currentPage + 1 },
) : PagerState(currentPage, currentPageOffsetFraction) {
    internal var pageCountLambda by mutableStateOf(pageCountLambda)
    override val pageCount get() = pageCountLambda()

    companion object {
        /** To keep current page and current page offset saved */
        val Saver: Saver<LessonPagerState, *> =
            listSaver(
                save = {
                    listOf(
                        it.currentPage,
                        it.currentPageOffsetFraction,
                        it.pageCount
                    )
                },
                restore = {
                    LessonPagerState(
                        currentPage = it[0] as Int,
                        currentPageOffsetFraction = it[1] as Float,
                        pageCountLambda = { it[2] as Int }
                    )
                }
            )
    }
}
