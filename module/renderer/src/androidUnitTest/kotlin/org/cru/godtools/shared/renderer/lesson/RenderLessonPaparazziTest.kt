package org.cru.godtools.shared.renderer.lesson

import androidx.compose.runtime.remember
import com.github.ajalt.colormath.model.RGB
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.junit.Test

class RenderLessonPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderLesson - Loaded`() {
        val manifest = Manifest(
            type = Manifest.Type.LESSON,
            resources = { listOf(resourceBruce) },
            pages = {
                listOf(
                    LessonPage(it),
                    LessonPage(it) {
                        listOf(
                            Text(it, text = "Top of Page"),
                            Spacer(),
                            Image(it, resource = "bruce"),
                            Spacer(),
                            Text(it, text = "Bottom of Page"),
                        )
                    },
                    LessonPage(it)
                )
            },
        )

        contentSnapshot {
            RenderLesson(
                LessonScreen.UiState.Loaded(
                    manifest,
                    state = remember { State() },
                    pagerState = rememberLessonPagerState(initialPage = 1),
                ),
            )
        }
    }

    @Test
    fun `RenderLesson - Loaded - Background Layers`() {
        val manifest = Manifest(
            type = Manifest.Type.LESSON,
            backgroundColor = RGB(0, 1, 1),
            backgroundImage = "black_panther",
            backgroundImageGravity = Gravity(Gravity.Horizontal.START, Gravity.Vertical.CENTER),
            backgroundImageScaleType = ImageScaleType.FILL,
            resources = { listOf(resourceBlackPanther) },
            pages = {
                listOf(
                    LessonPage(
                        it,
                        backgroundColor = RGB(1, 0, 0, 0.12f),
                        backgroundImage = "black_panther",
                        backgroundImageGravity = Gravity.CENTER,
                        backgroundImageScaleType = ImageScaleType.FIT,
                        textColor = RGB(1, 1, 0),
                    ) {
                        listOf(
                            Text(it, text = "Top of Page"),
                            Spacer(),
                            Text(it, text = "Center"),
                            Spacer(),
                            Text(it, text = "Bottom of Page"),
                        )
                    },
                    LessonPage(it),
                )
            },
        )

        contentSnapshot {
            RenderLesson(
                LessonScreen.UiState.Loaded(
                    manifest,
                    state = remember { State() },
                    pagerState = rememberLessonPagerState(),
                ),
            )
        }
    }

    @Test
    fun `RenderLesson - Loading`() {
        contentSnapshot {
            RenderLesson(LessonScreen.UiState.Loading(progress = 0.316f))
        }
    }

    @Test
    fun `RenderLesson - Loading - Indeterminate`() {
        animatedContentSnapshot(end = 1_000) {
            RenderLesson(LessonScreen.UiState.Loading(progress = null))
        }
    }

    @Test
    fun `RenderLesson - Missing`() {
        contentSnapshot {
            RenderLesson(LessonScreen.UiState.Missing())
        }
    }

    @Test
    fun `RenderLesson - Offline`() {
        contentSnapshot {
            RenderLesson(LessonScreen.UiState.Offline())
        }
    }
}
