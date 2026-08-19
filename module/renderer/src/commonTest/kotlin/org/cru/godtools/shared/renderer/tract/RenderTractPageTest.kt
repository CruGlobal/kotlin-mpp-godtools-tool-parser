package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.runCurrent
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_type_tip
import org.cru.godtools.shared.renderer.generated.resources.tract_accessibility_action_next_page
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tract.CallToAction
import org.cru.godtools.shared.tool.parser.model.tract.Modal
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class RenderTractPageTest : BaseRendererTest() {
    private val events = mutableListOf<TractPageEvent>()

    @Test
    fun `Action - card label - tap opens the card and emits CardTapped event`() = runComposeUiTest {
        val page = TractPage(
            cards = { p -> listOf(TractPage.Card(p, 0, label = { c -> Text(c, text = "Card 1") })) },
        )
        val pageState = TractPageState(page)

        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(pageState, state = state, pageEvents = { events += it })
            }
        }

        onNodeWithText("Card 1").performClick()
        waitForIdle()
        assertEquals(page.cards[0], pageState.activeCard, "tapping the label should open the card")
        assertEquals(TractPageEvent.CardTapped, events.single(), "toggle should emit CardTapped event")

        onNodeWithText("Card 1").performClick()
        waitForIdle()
        assertNull(pageState.activeCard, "tapping the label again should close the card")
    }

    @Test
    fun `Event - card listeners - content event reveals hidden card`() = runComposeUiTest {
        val showCard = EventId(name = "show-card")
        val page = TractPage(
            cards = { p ->
                listOf(
                    TractPage.Card(p, 0),
                    TractPage.Card(p, 1, isHidden = true, listeners = setOf(showCard)),
                )
            },
        )
        val pageState = TractPageState(page)
        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(pageState, state = state, pageEvents = { events += it })
            }
        }

        state.triggerContentEvents(listOf(showCard))
        testScope.runCurrent()
        waitForIdle()
        assertEquals(page.cards[1], pageState.activeCard, "content event should reveal the hidden card")
        assertEquals(2, onAllNodesWithTag(TestTagTractCard).fetchSemanticsNodes().size, "both cards should compose")
    }

    @Test
    fun `Event - dismissListeners - content event dismisses the active card`() = runComposeUiTest {
        val dismiss = EventId(name = "dismiss")
        val page = TractPage(
            cards = { p -> listOf(TractPage.Card(p, 0, dismissListeners = setOf(dismiss))) },
        )
        val pageState = TractPageState(page)
        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(pageState, state = state, pageEvents = { events += it })
            }
        }
        pageState.navigateToCard(page.cards[0])
        waitForIdle()

        state.triggerContentEvents(listOf(dismiss))
        testScope.runCurrent()
        waitForIdle()
        assertNull(pageState.activeCard, "content event should dismiss the active card")
    }

    @Test
    fun `Event - content event modal listener - emits OpenModal page event`() = runComposeUiTest {
        val showModal = EventId(name = "show-modal")
        val page = TractPage(modals = { p -> listOf(Modal(p, listeners = setOf(showModal))) })

        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(page, state = state, pageEvents = { events += it })
            }
        }

        state.triggerContentEvents(listOf(showModal))
        testScope.runCurrent()
        waitForIdle()
        assertEquals(
            listOf<TractPageEvent>(TractPageEvent.OpenModal(page.modals[0])),
            events,
            "modal listener content event should emit OpenModal to the page events callback",
        )
    }

    @Test
    fun `Action - call to action arrow - emits GoToNextPage`() = runComposeUiTest {
        val manifest = Manifest(pages = { listOf(TractPage(it), TractPage(it)) })
        val page = manifest.pages.first() as TractPage

        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(page, state = state, pageEvents = { events += it })
            }
        }

        onNodeWithContentDescription(getString(Res.string.tract_accessibility_action_next_page)).performClick()
        assertEquals(listOf<TractPageEvent>(TractPageEvent.GoToNextPage), events, "arrow tap should emit GoToNextPage")
    }

    @Test
    fun `Analytics - hero ScreenView - fires while no card is active`() = runComposeUiTest {
        // build the page through a Manifest so page.position resolves to 0 (screen name "tool-0")
        val manifest = Manifest(code = "tool", pages = { listOf(TractPage(it)) })
        val page = manifest.pages.first() as TractPage

        lifecycleOwner.currentState = Lifecycle.State.STARTED
        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(page, state = state)
            }
        }

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ScreenView>().test {
            lifecycleOwner.currentState = Lifecycle.State.RESUMED
            assertEquals("tool-0", awaitItem().screenName, "hero should fire the page-level ScreenView")
        }
    }

    @Test
    fun `Event - CardSwiped - fling emits CardSwiped`() = runComposeUiTest {
        val page = TractPage(
            cards = { p -> listOf(TractPage.Card(p, 0, label = { c -> Text(c, text = "Card 1") })) },
        )
        val pageState = TractPageState(page)

        setContent {
            ProvideTestCompositionLocals {
                Box(Modifier.size(300.dp, 450.dp)) {
                    RenderTractPage(
                        pageState,
                        state = state,
                        pageEvents = { events += it },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("page")
                    )
                }
            }
        }

        onNodeWithTag("page").performTouchInput { swipeUp() }
        waitForIdle()
        assertEquals(1, events.count { it == TractPageEvent.CardSwiped }, "fling should emit exactly one CardSwiped")
    }

    @Test
    fun `UI - call to action tip - follows the CTA into view`() = runComposeUiTest {
        mainClock.autoAdvance = false
        val manifest = Manifest(tips = { listOf(Tip(it, id = "cta-tip")) })
        val page = TractPage(
            manifest,
            cards = { p -> listOf(TractPage.Card(p, 0), TractPage.Card(p, 1)) },
            callToAction = { CallToAction(it, tip = "cta-tip") },
        )
        val pageState = TractPageState(page)
        state.showTips.value = true

        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(pageState, state = state)
            }
        }
        mainClock.advanceTimeBy(1_000)
        // the CTA tip's contentDescription is the localized tip-type name; the fixture tip defaults to Type.TIP
        val tipDescription = getString(Res.string.tool_renderer_tip_type_tip)
        onNodeWithContentDescription(tipDescription).assertIsNotDisplayed()

        pageState.navigateToCard(page.cards.last())
        // the CTA (and its tip) fade in only after the card-change offset animation settles; pump frames
        // with an interleaved semantics read, matching TractPageLayoutTest's CTA fade-in coverage
        repeat(60) {
            mainClock.advanceTimeBy(16)
            onAllNodesWithTag(TestTagTractCard).fetchSemanticsNodes()
        }
        onNodeWithContentDescription(tipDescription).assertIsDisplayed()
    }
}
