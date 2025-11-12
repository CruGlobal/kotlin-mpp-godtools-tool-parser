package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.filterIsInstance
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.Header
import org.cru.godtools.shared.tool.parser.model.tract.Hero
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderTractHeroTest : BaseRendererTest() {
    private fun SemanticsNodeInteractionsProvider.onHeaderNode() = onNodeWithTag(TestTagHeader)
    private fun SemanticsNodeInteractionsProvider.onHeaderNumberNode() = onNodeWithTag(TestTagHeaderNumber)
    private fun SemanticsNodeInteractionsProvider.onHeaderTitleNode() = onNodeWithTag(TestTagHeaderTitle)
    private fun SemanticsNodeInteractionsProvider.onHeroHeadingNode() = onNodeWithTag(TestTagHeroHeading)

    @Test
    fun `UI - Header - Number`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(number = { Text(text = "1") }) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderNumberNode().assertExists()
    }

    @Test
    fun `UI - Header - Number - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(number = null) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderNumberNode().assertDoesNotExist()
    }

    @Test
    fun `UI - Header - Title`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(title = { Text(text = "title") }) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderTitleNode().assertExists()
    }

    @Test
    fun `UI - Header - Title - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(title = null) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderTitleNode().assertDoesNotExist()
    }

    @Test
    fun `UI - Header - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = null))
            }
        }

        onHeaderNode().assertDoesNotExist()
    }

    @Test
    fun `UI - Hero - Heading`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(hero = { Hero(heading = { Text(text = "heading") }) }))
            }
        }

        onHeroHeadingNode().assertExists()
    }

    @Test
    fun `UI - Hero - Heading - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(hero = { Hero(heading = null) }))
            }
        }

        onHeroHeadingNode().assertDoesNotExist()
    }

    // region UI - AnalyticsEvents - Content
    private val analyticsEventVisible = AnalyticsEvent("page_visible", trigger = AnalyticsEvent.Trigger.VISIBLE)
    private val analyticsEventDelayed = AnalyticsEvent(
        "visible_delayed",
        trigger = AnalyticsEvent.Trigger.VISIBLE,
        delay = 1
    )

    @Test
    fun `UI - AnalyticsEvents - Content`() = runComposeUiTest {
        val page = TractPage(hero = { Hero(analyticsEvents = listOf(analyticsEventVisible)) })

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ContentEvent>().test {
            setContent {
                ProvideTestCompositionLocals {
                    RenderTractHero(page, state = state)
                }
            }

            assertEquals(analyticsEventVisible, awaitItem().event)
        }
    }

    @Test
    fun `UI - AnalyticsEvents - Content - Trigger when lifecycle is resumed`() = runComposeUiTest {
        lifecycleOwner.currentState = Lifecycle.State.STARTED
        val page = TractPage(hero = { Hero(analyticsEvents = listOf(analyticsEventVisible)) })

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ContentEvent>().test {
            setContent {
                ProvideTestCompositionLocals {
                    RenderTractHero(page, state = state)
                }
            }
            expectNoEvents()

            // resume the lifecycle and ensure the event is triggered
            lifecycleOwner.currentState = Lifecycle.State.RESUMED
            assertEquals(analyticsEventVisible, awaitItem().event)
        }
    }

    @Test
    fun `UI - AnalyticsEvents - Content - Delayed`() = runComposeUiTest {
        val page = TractPage(hero = { Hero(analyticsEvents = listOf(analyticsEventDelayed)) })

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ContentEvent>().test {
            setContent {
                ProvideTestCompositionLocals {
                    RenderTractHero(page, state = state)
                }
            }

            // event should not be triggered immediately
            mainClock.advanceTimeBy(900)
            expectNoEvents()

            // advance past the delay and ensure the event is triggered
            mainClock.advanceTimeBy(200)
            assertEquals(analyticsEventDelayed, awaitItem().event)
        }
    }

    @Test
    fun `UI - AnalyticsEvents - Content - Delayed - Canceled when lifecycle is paused`() = runComposeUiTest {
        val page = TractPage(hero = { Hero(analyticsEvents = listOf(analyticsEventDelayed)) })

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ContentEvent>().test {
            setContent {
                ProvideTestCompositionLocals {
                    RenderTractHero(page, state = state)
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
    // endregion UI - AnalyticsEvents - Content
}
