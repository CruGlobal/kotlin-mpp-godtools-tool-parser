package org.cru.godtools.shared.renderer.lesson

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_page_next
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_page_previous
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderLessonPageTest : BaseRendererTest() {
    private val manifest = Manifest(
        pages = { manifest ->
            List(3) { i ->
                LessonPage(manifest) {
                    listOf(
                        Text(it, text = "Page ${i + 1}"),
                    )
                }
            }
        }
    )
    private val pageEvents = mutableListOf<LessonPageEvent>()

    @Test
    fun `UI - Content - Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderLessonPage(manifest.pages[0] as LessonPage)
            }
        }
        onNodeWithText("Page 1").assertIsDisplayed()
    }

    private val previousPage by lazy { runBlocking { getString(Res.string.lesson_accessibility_action_page_previous) } }
    private val nextPage by lazy { runBlocking { getString(Res.string.lesson_accessibility_action_page_next) } }

    @Test
    fun `UI - Navigation`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderLessonPage(manifest.pages[1] as LessonPage) { pageEvents.add(it) }
            }
        }

        assertTrue(pageEvents.isEmpty())
        onNodeWithContentDescription(previousPage)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertEquals(listOf<LessonPageEvent>(LessonPageEvent.PreviousPage), pageEvents)

        pageEvents.clear()
        onNodeWithContentDescription(nextPage)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertEquals(listOf<LessonPageEvent>(LessonPageEvent.NextPage), pageEvents)
    }

    @Test
    fun `UI - Navigation - Previous - Not Present on First Page`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderLessonPage(manifest.pages.first() as LessonPage)
            }
        }

        onNodeWithContentDescription(previousPage).assertDoesNotExist()
    }

    @Test
    fun `UI - Navigation - Next - Not Present on Last Page`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderLessonPage(manifest.pages.last() as LessonPage)
            }
        }

        onNodeWithContentDescription(nextPage).assertDoesNotExist()
    }

    // region UI - AnalyticsEvents
    private val analyticsEventVisible = AnalyticsEvent("page_visible", trigger = AnalyticsEvent.Trigger.VISIBLE)
    private val analyticsEventDelayed = AnalyticsEvent(
        "delayed_event",
        trigger = AnalyticsEvent.Trigger.VISIBLE,
        delay = 1
    )

    @Test
    fun `UI - AnalyticsEvents`() = runComposeUiTest {
        val page = LessonPage(
            manifest,
            analyticsEvents = listOf(analyticsEventVisible),
        )

        testScope.runTest {
            state.events.test {
                setContent {
                    ProvideTestCompositionLocals {
                        RenderLessonPage(page, state = state)
                    }
                }

                assertEquals(State.Event.AnalyticsEvent.ContentEvent(analyticsEventVisible), awaitItem())
            }
        }
    }

    @Test
    fun `UI - AnalyticsEvents - Trigger when lifecycle is resumed`() = runComposeUiTest {
        lifecycleOwner.currentState = Lifecycle.State.STARTED
        val page = LessonPage(
            manifest,
            analyticsEvents = listOf(analyticsEventVisible),
        )

        testScope.runTest {
            state.events.test {
                setContent {
                    ProvideTestCompositionLocals {
                        RenderLessonPage(page, state = state)
                    }
                }
                expectNoEvents()

                // resume the lifecycle and ensure the event is triggered
                lifecycleOwner.currentState = Lifecycle.State.RESUMED
                assertEquals(State.Event.AnalyticsEvent.ContentEvent(analyticsEventVisible), awaitItem())
            }
        }
    }

    @Test
    fun `UI - AnalyticsEvents - Delayed`() = runComposeUiTest {
        val page = LessonPage(
            manifest,
            analyticsEvents = listOf(analyticsEventDelayed),
        )

        testScope.runTest {
            state.events.test {
                setContent {
                    ProvideTestCompositionLocals {
                        RenderLessonPage(page, state = state)
                    }
                }

                // event should not be triggered immediately
                mainClock.advanceTimeBy(900)
                expectNoEvents()

                // advance past the delay and ensure the event is triggered
                mainClock.advanceTimeBy(200)
                assertEquals(State.Event.AnalyticsEvent.ContentEvent(analyticsEventDelayed), awaitItem())
            }
        }
    }

    @Test
    fun `UI - AnalyticsEvents - Delayed - Canceled when lifecycle is paused`() = runComposeUiTest {
        val page = LessonPage(
            manifest,
            analyticsEvents = listOf(analyticsEventDelayed),
        )

        testScope.runTest {
            state.events.test {
                setContent {
                    ProvideTestCompositionLocals {
                        RenderLessonPage(page, state = state)
                    }
                }

                // event should not be triggered immediately
                mainClock.advanceTimeBy(900)
                expectNoEvents()

                // advance past the delay, but since the lifecycle is not resumed the event should not be triggered
                lifecycleOwner.currentState = Lifecycle.State.STARTED
                mainClock.advanceTimeBy(200)
                expectNoEvents()
            }
        }
    }
    // endregion UI - AnalyticsEvents
}
