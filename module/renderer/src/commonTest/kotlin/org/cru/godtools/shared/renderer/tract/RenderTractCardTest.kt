package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.lifecycle.Lifecycle
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.filterIsInstance
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_next
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_previous
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tract.CallToAction
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderTractCardTest : BaseRendererTest() {
    private var toggleCalled = 0
    private var previousCalled = 0
    private var nextCalled = 0

    private fun page(cards: (TractPage) -> List<TractPage.Card>) = TractPage(cards = cards)

    @Test
    fun `Action - label - tap toggles card`() = runComposeUiTest {
        val page = TractPage(
            cards = {
                listOf(TractPage.Card(it, 0, label = { p -> Text(p, text = "Card 1") }))
            }
        )
        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(
                    page.cards.first(),
                    state = state,
                    onToggleCard = { toggleCalled++ },
                    onPreviousCard = { previousCalled++ },
                    onNextCard = { nextCalled++ },
                )
            }
        }

        onNodeWithText("Card 1", useUnmergedTree = true).performClick()
        assertEquals(1, toggleCalled, "clicking the label should invoke onToggleCard")
    }

    @Test
    fun `Action - previous - tap triggers onPreviousCard`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0), TractPage.Card(it, 1), TractPage.Card(it, 2)) }
        val previousText = getString(Res.string.tract_card_action_previous)
        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(
                    page.cards[1],
                    state = state,
                    onToggleCard = { toggleCalled++ },
                    onPreviousCard = { previousCalled++ },
                    onNextCard = { nextCalled++ },
                )
            }
        }

        onNodeWithText(previousText).performClick()
        assertEquals(1, previousCalled, "tapping previous should invoke onPreviousCard")
        assertEquals(0, nextCalled, "tapping previous should not invoke onNextCard")
    }

    @Test
    fun `Action - next - tap triggers onNextCard`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0), TractPage.Card(it, 1), TractPage.Card(it, 2)) }
        val nextText = getString(Res.string.tract_card_action_next)
        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(
                    page.cards[1],
                    state = state,
                    onToggleCard = { toggleCalled++ },
                    onPreviousCard = { previousCalled++ },
                    onNextCard = { nextCalled++ },
                )
            }
        }

        onNodeWithText(nextText).performClick()
        assertEquals(1, nextCalled, "tapping next should invoke onNextCard")
        assertEquals(0, previousCalled, "tapping next should not invoke onPreviousCard")
    }

    @Test
    fun `UI - position - shows visible position and count`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0), TractPage.Card(it, 1), TractPage.Card(it, 2)) }

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(page.cards[1], state, {}, {}, {})
            }
        }

        onNodeWithText("2/3").assertExists(
            "position indicator should show the card's 1-based visible position and total visible card count",
        )
    }

    @Test
    fun `UI - nav - previous suppressed on first visible card`() = runComposeUiTest {
        val page = page {
            listOf(TractPage.Card(it, 0), TractPage.Card(it, 1, isHidden = true), TractPage.Card(it, 2))
        }
        val previousText = getString(Res.string.tract_card_action_previous)
        val nextText = getString(Res.string.tract_card_action_next)

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(page.cards[0], state, {}, {}, {})
            }
        }

        onNodeWithText(previousText).assertIsNotEnabled()
        onNodeWithText(nextText).assertIsEnabled()
    }

    @Test
    fun `UI - nav - next suppressed on last visible card`() = runComposeUiTest {
        val page = page {
            listOf(TractPage.Card(it, 0), TractPage.Card(it, 1, isHidden = true), TractPage.Card(it, 2))
        }
        val previousText = getString(Res.string.tract_card_action_previous)
        val nextText = getString(Res.string.tract_card_action_next)

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(page.cards[2], state, {}, {}, {})
            }
        }

        onNodeWithText(nextText).assertIsNotEnabled()
        onNodeWithText(previousText).assertIsEnabled()
    }

    @Test
    fun `UI - nav - hidden card suppresses previous next and position`() = runComposeUiTest {
        val page = page {
            listOf(TractPage.Card(it, 0), TractPage.Card(it, 1, isHidden = true), TractPage.Card(it, 2))
        }
        val previousText = getString(Res.string.tract_card_action_previous)
        val nextText = getString(Res.string.tract_card_action_next)

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(page.cards[1], state, {}, {}, {})
            }
        }

        onNodeWithText(previousText).assertIsNotEnabled()
        onNodeWithText(nextText).assertIsNotEnabled()
        onNodeWithText(previousText)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility))
        onNodeWithText("1/2")
            .assert(hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)))
        onNodeWithText(nextText)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility))
    }

    @Test
    fun `Analytics - ScreenView - resume triggers ScreenView for the card`() = runComposeUiTest {
        // build the page through a Manifest so page.position resolves to 0 (screen name "tool-0a")
        val manifest = Manifest(
            code = "tool",
            pages = { m -> listOf(TractPage(m, cards = { p -> listOf(TractPage.Card(p, 0)) })) },
        )
        val page = manifest.pages.first() as TractPage

        lifecycleOwner.currentState = Lifecycle.State.STARTED
        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(page.cards.first(), state, {}, {}, {})
            }
        }

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ScreenView>().test {
            lifecycleOwner.currentState = Lifecycle.State.RESUMED
            assertEquals(
                "tool-0a",
                awaitItem().screenName,
                "screen name should combine the tool code, page position, and card letter",
            )
        }
    }

    @Test
    fun `Analytics - hidden events - pause triggers HIDDEN analytics events for the card`() = runComposeUiTest {
        val hiddenEvent = AnalyticsEvent("card_hidden", trigger = AnalyticsEvent.Trigger.HIDDEN)
        val page = page { listOf(TractPage.Card(it, 0, analyticsEvents = listOf(hiddenEvent))) }

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(page.cards.first(), state, {}, {}, {})
            }
        }

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ContentEvent>().test {
            expectNoEvents()

            lifecycleOwner.currentState = Lifecycle.State.STARTED
            assertEquals(
                hiddenEvent,
                awaitItem().event,
                "pausing the card should trigger its HIDDEN analytics events",
            )
        }
    }

    @Test
    fun `UI - tip indicator - content tip shown only when showTips is enabled`() = runComposeUiTest {
        val manifest = Manifest(tips = { listOf(Tip(it, id = "tip1")) })
        val page = TractPage(manifest)
        val card = TractPage.Card(page, 0, content = { listOf(InlineTip(it, id = "tip1")) })

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(card, state, {}, {}, {})
            }
        }

        onNodeWithTag(TestTagTractCardTipIndicator, useUnmergedTree = true).assertDoesNotExist()
        state.showTips.value = true
        onNodeWithTag(TestTagTractCardTipIndicator, useUnmergedTree = true).assertExists()
    }

    @Test
    fun `UI - tip indicator - call-to-action tip fallback shown on last visible card when showTips is enabled`() =
        runComposeUiTest {
            val manifest = Manifest(tips = { listOf(Tip(it, id = "cta-tip")) })
            val page = TractPage(
                manifest,
                cards = { listOf(TractPage.Card(it, 0)) },
                callToAction = { CallToAction(it, tip = "cta-tip") },
            )
            val card = page.cards.first()

            setContent {
                ProvideTestCompositionLocals {
                    RenderTractCard(card, state, {}, {}, {})
                }
            }

            onNodeWithTag(TestTagTractCardTipIndicator, useUnmergedTree = true).assertDoesNotExist()
            state.showTips.value = true
            onNodeWithTag(TestTagTractCardTipIndicator, useUnmergedTree = true).assertExists()
        }

    @Test
    fun `Layout - alignment lines - published in expected order`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0, label = { p -> Text(p, text = "Card 1") })) }
        var padding = -1
        var peek = -1
        var stack = -1

        setContent {
            ProvideTestCompositionLocals {
                Layout(
                    content = { RenderTractCard(page.cards.first(), state, {}, {}, {}) },
                ) { measurables, constraints ->
                    val placeable = measurables.single().measure(constraints)
                    padding = placeable[TractCardPaddingLine]
                    peek = placeable[TractCardPeekLine]
                    stack = placeable[TractCardStackLine]
                    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                }
            }
        }

        assertTrue(padding > 0, "padding line should be below the top margin")
        assertTrue(peek > padding, "peek line (label top) should be below the card surface top")
        assertTrue(stack > peek, "stack line (divider top) should be below the label top")
    }
}
