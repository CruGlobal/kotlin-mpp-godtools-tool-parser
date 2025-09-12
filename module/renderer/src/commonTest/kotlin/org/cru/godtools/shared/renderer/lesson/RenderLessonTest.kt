package org.cru.godtools.shared.renderer.lesson

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.slack.circuit.test.TestEventSink
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runCurrent
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_close
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_page_next
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_page_previous
import org.cru.godtools.shared.renderer.generated.resources.tool_not_found
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Manifest.Type
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class RenderLessonTest : BaseRendererTest() {
    private companion object {
        fun hasPageId(id: String) = SemanticsMatcher("pageId == $id") {
            it.config.getOrElseNullable(LessonPageId) { null } == id
        }

        fun SemanticsNodeInteractionsProvider.onPager() = onNodeWithTag(TestTagLessonPager)
        fun SemanticsNodeInteractionsProvider.onLessonPage(id: String) =
            onNode(hasTestTag(TestTagLessonPage) and hasPageId(id))
    }

    private val eventSink = TestEventSink<LessonScreen.UiEvent>()
    private val pagerState = LessonPagerState()

    private val closeLesson by lazy { runBlocking { getString(Res.string.lesson_accessibility_action_close) } }
    private val previousPage by lazy { runBlocking { getString(Res.string.lesson_accessibility_action_page_previous) } }
    private val nextPage by lazy { runBlocking { getString(Res.string.lesson_accessibility_action_page_next) } }

    @Test
    fun `UI - AppBar Close Button`() = runComposeUiTest {
        val state = LessonScreen.UiState.Loaded(
            manifest = Manifest(type = Type.LESSON),
            state = state,
            pagerState = pagerState,
            eventSink = eventSink::invoke,
        )
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(state)
            }
        }

        onNodeWithContentDescription(closeLesson).assertExists().performClick()
        eventSink.assertEvent(LessonScreen.UiEvent.CloseLesson)
    }

    @Test
    fun `UI - Missing`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(LessonScreen.UiState.Missing())
            }
        }

        onPager().assertDoesNotExist()
        onNodeWithText(runBlocking { getString(Res.string.tool_not_found) }).assertIsDisplayed()
    }

    @Test
    fun `UI - Loaded - HorizontalPager`() = runComposeUiTest {
        val manifest = Manifest(
            type = Type.LESSON,
            pages = {
                listOf(
                    LessonPage(it, id = "page1"),
                    LessonPage(it, id = "page2"),
                )
            },
        )
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(LessonScreen.UiState.Loaded(manifest, state, pagerState = pagerState))
            }
        }

        onPager().assertExists()
        assertEquals(2, pagerState.pageCount)
        assertEquals(0, pagerState.currentPage)

        onLessonPage("page1").assertIsDisplayed()
        onLessonPage("page2").assertIsNotDisplayed()
    }

    @Test
    fun `UI - Loaded - HorizontalPager - Hide Hidden Pages`() = runComposeUiTest {
        val manifest = Manifest(
            type = Type.LESSON,
            pages = {
                listOf(
                    LessonPage(id = "page1", isHidden = true),
                    LessonPage(id = "page2", isHidden = false),
                    LessonPage(id = "page3", isHidden = true),
                )
            },
        )
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(LessonScreen.UiState.Loaded(manifest, state, pagerState = pagerState))
            }
        }

        onPager().assertExists()
        assertEquals(1, pagerState.pageCount)
        assertEquals(0, pagerState.currentPage)
        onLessonPage("page2").assertIsDisplayed()
    }

    @Test
    fun `UI - Loaded - HorizontalPager - Content Events`() = runComposeUiTest {
        val event = EventId(name = "content_event")
        val manifest = Manifest(
            type = Type.LESSON,
            pages = {
                listOf(
                    LessonPage(it, id = "page1"),
                    LessonPage(it, id = "page2", listeners = setOf(event)),
                )
            },
        )

        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(LessonScreen.UiState.Loaded(manifest, state, pagerState = pagerState))
            }
        }

        onLessonPage("page1").assertIsDisplayed()

        state.triggerContentEvents(listOf(event))
        testScope.runCurrent()

        onLessonPage("page2").assertIsDisplayed()
    }

    @Test
    fun `UI - Loaded - HorizontalPager - Content Events - Hidden Page`() = runComposeUiTest {
        val event1 = EventId(name = "content_event")
        val event2 = EventId(name = "content_event2")
        val manifest = Manifest(
            type = Type.LESSON,
            pages = {
                listOf(
                    LessonPage(it, id = "page1"),
                    LessonPage(it, id = "page2", isHidden = true, listeners = setOf(event1)),
                    LessonPage(it, id = "page3", listeners = setOf(event2)),
                )
            },
        )

        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(LessonScreen.UiState.Loaded(manifest, state, pagerState = pagerState))
            }
        }

        assertEquals(2, pagerState.pageCount)
        assertEquals(0, pagerState.currentPage)
        onLessonPage("page1").assertIsDisplayed()

        state.triggerContentEvents(listOf(event1))
        testScope.runCurrent()
        waitForIdle()

        assertEquals(3, pagerState.pageCount)
        assertEquals(1, pagerState.currentPage)
        onLessonPage("page2").assertIsDisplayed()

        state.triggerContentEvents(listOf(event2))
        testScope.runCurrent()
        waitForIdle()

        assertEquals(2, pagerState.pageCount)
        assertEquals(1, pagerState.currentPage)
        onLessonPage("page3").assertIsDisplayed()
    }

    @Test
    fun `UI - Loaded - HorizontalPager - Page Navigation`() = runComposeUiTest {
        val manifest = Manifest(
            type = Type.LESSON,
            pages = {
                listOf(
                    LessonPage(it, id = "page1"),
                    LessonPage(it, id = "page2"),
                )
            },
        )
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(LessonScreen.UiState.Loaded(manifest, state, pagerState = pagerState))
            }
        }

        assertEquals(2, pagerState.pageCount)
        assertEquals(0, pagerState.currentPage)
        onLessonPage("page1").assertIsDisplayed()
        onLessonPage("page2").assertIsNotDisplayed()

        onNodeWithContentDescription(nextPage).performClick()
        waitForIdle()
        assertEquals(1, pagerState.currentPage)
        onLessonPage("page1").assertIsNotDisplayed()
        onLessonPage("page2").assertIsDisplayed()

        onNodeWithContentDescription(previousPage).performClick()
        waitForIdle()
        assertEquals(0, pagerState.currentPage)
        onLessonPage("page1").assertIsDisplayed()
        onLessonPage("page2").assertIsNotDisplayed()
    }
}
