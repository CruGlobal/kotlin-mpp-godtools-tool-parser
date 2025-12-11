package org.cru.godtools.shared.renderer.lesson

import com.github.ajalt.colormath.model.RGB
import io.fluidsonic.locale.Locale
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage

class RenderLessonPaparazziTest : BasePaparazziTest() {
    private val lessonManifest = Manifest(
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
    private val state = State()

    @Test
    fun `RenderLesson - Loaded`() {
        contentSnapshot {
            RenderLesson(
                LessonScreen.UiState.Loaded(
                    lessonManifest,
                    state = state,
                    showShareAction = true,
                    lessonPager = rememberLessonPagerState(lessonManifest, initialPage = 1),
                ),
            )
        }
    }

    @Test
    fun `RenderLesson - Loaded - No Share Action`() {
        contentSnapshot {
            RenderLesson(
                LessonScreen.UiState.Loaded(
                    lessonManifest,
                    state = state,
                    showShareAction = false,
                    lessonPager = rememberLessonPagerState(lessonManifest, initialPage = 1),
                ),
            )
        }
    }

    @Test
    fun `RenderLesson - Loaded - Background Layers`() {
        val manifest = Manifest(
            type = Manifest.Type.LESSON,
            navBarColor = RGB(0, 0, 0, 0.5f),
            navBarControlColor = RGB(1, 1, 1),
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
                    state = state,
                    showShareAction = true,
                    lessonPager = rememberLessonPagerState(manifest),
                ),
            )
        }
    }

    @Test
    fun `RenderLesson - Loaded - RTL`() {
        val manifest = Manifest(
            type = Manifest.Type.LESSON,
            locale = Locale.forLanguage("ar"),
            pages = {
                listOf(
                    LessonPage(it) {
                        listOf(
                            Text(it, text = "Start", textAlign = Text.Align.START),
                            Text(it, text = "Center", textAlign = Text.Align.CENTER),
                            Text(it, text = "End", textAlign = Text.Align.END),
                        )
                    },
                    LessonPage(it),
                    LessonPage(it),
                )
            },
        )

        contentSnapshot {
            RenderLesson(
                LessonScreen.UiState.Loaded(
                    manifest,
                    state = state,
                    showShareAction = true,
                    lessonPager = rememberLessonPagerState(manifest),
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
