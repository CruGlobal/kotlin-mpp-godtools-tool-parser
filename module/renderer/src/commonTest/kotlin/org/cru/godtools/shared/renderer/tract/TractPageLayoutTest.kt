package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_next
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class TractPageLayoutTest : BaseRendererTest() {
    private val page = TractPage(
        cards = { page ->
            (0..2).map { TractPage.Card(page, it, label = { p -> Text(p, text = "Card $it") }) }
        },
    )

    // fixed and small enough to fit within every platform's default compose-ui-test window
    private val containerWidth = 300.dp
    private val layoutHeight = 450.dp

    private fun ComposeUiTest.setTestContent(pageState: TractPageState, onSwipe: () -> Unit = {}) = setContent {
        ProvideTestCompositionLocals {
            Box(Modifier.size(containerWidth, layoutHeight).testTag("container")) {
                TractPageLayout(
                    pageState = pageState,
                    hero = { Box(Modifier.fillMaxSize().testTag("hero")) },
                    callToAction = { Box(Modifier.fillMaxWidth().height(56.dp).testTag("cta")) },
                    card = { card ->
                        Box(Modifier.testTag("card-${card.position}")) { RenderTractCard(card, state, {}, {}, {}) }
                    },
                    onCardSwipe = onSwipe,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("layout")
                )
            }
        }
    }

    // resolves a specific card's own surface node by index, independent of sibling placement/ordering
    private fun SemanticsNodeInteractionsProvider.cardSurface(index: Int) =
        onNode(hasTestTag(TestTagTractCard) and hasAnyAncestor(hasTestTag("card-$index")))

    // advances the clock frame by frame, waiting for idle between frames; a single large mainClock jump only
    // resumes animations already awaiting a frame, it never drains the dispatcher hops that launch new ones
    // (snapshotFlow emissions, delay resumptions, chained animateTo calls)
    private fun ComposeUiTest.advanceTimeBy(millis: Long) {
        val end = mainClock.currentTime + millis
        while (mainClock.currentTime < end) {
            mainClock.advanceTimeByFrame()
            waitForIdle()
        }
    }

    @Test
    fun `UI - hero - visible with cards stacked at the bottom`() = runComposeUiTest {
        val pageState = TractPageState(page)
        setTestContent(pageState)

        val container = onNodeWithTag("container").getBoundsInRoot()
        val heroBounds = onNodeWithTag("hero").getBoundsInRoot()
        assertEquals(container.top, heroBounds.top, "hero should be at the top")

        val cardTops = (0..2).map { cardSurface(it).getBoundsInRoot().top }
        assertTrue(
            cardTops.all { it > container.top + layoutHeight / 2 },
            "stacked cards should sit in the bottom half",
        )
        assertTrue(cardTops[0] < cardTops[1], "labels should stack in order")
        assertTrue(cardTops[1] < cardTops[2], "labels should stack in order")
    }

    @Test
    fun `UI - active card - open with next card peeking and hero offscreen`() = runComposeUiTest {
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[0]) }
        setTestContent(pageState)

        val container = onNodeWithTag("container").getBoundsInRoot()
        onNodeWithTag("hero").assertIsNotDisplayed()

        val activeTop = cardSurface(0).getBoundsInRoot().top
        assertEquals(
            container.top + 16.dp,
            activeTop,
            "active card should be at the top, below its own 16dp margin (RenderTractCard's own padding)"
        )

        val peekTop = cardSurface(1).getBoundsInRoot().top
        assertTrue(peekTop > container.bottom - 100.dp, "next card should peek at the bottom edge")
    }

    // region UI - Call To Action
    @Test
    fun `UI - call to action - shown at the bottom edge when last card is active`() = runComposeUiTest {
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[2]) }
        setTestContent(pageState)

        val container = onNodeWithTag("container").getBoundsInRoot()
        val ctaBounds = onNodeWithTag("cta").assertIsDisplayed().getBoundsInRoot()
        assertEquals(container.bottom, ctaBounds.bottom, "CTA should sit at the bottom edge")
    }

    @Test
    fun `UI - call to action - hidden while stacked cards cover it`() = runComposeUiTest {
        val pageState = TractPageState(page)
        setTestContent(pageState)

        onNodeWithTag("cta").assertIsNotDisplayed()
    }

    @Test
    fun `UI - call to action - shown when page has no cards`() = runComposeUiTest {
        val emptyPage = TractPage(cards = { emptyList() })
        val pageState = TractPageState(emptyPage)
        setTestContent(pageState)

        onNodeWithTag("cta").assertIsDisplayed()
    }
    // endregion UI - Call To Action

    // region Action - Fling
    @Test
    fun `Action - fling - swipe up opens the first card`() = runComposeUiTest {
        val pageState = TractPageState(page)
        var swiped = 0
        setTestContent(pageState, onSwipe = { swiped++ })

        onNodeWithTag("layout").performTouchInput { swipeUp() }
        waitForIdle()
        assertEquals(page.cards[0], pageState.activeCard, "swipe up should open the first card")
        assertEquals(1, swiped, "onCardSwiped should fire once")
    }

    @Test
    fun `Action - fling - swipe down closes the active card`() = runComposeUiTest {
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[0]) }
        setTestContent(pageState)

        onNodeWithTag("layout").performTouchInput { swipeDown() }
        waitForIdle()
        assertNull(pageState.activeCard, "swipe down should close the active card")
    }

    @Test
    fun `Action - fling - swipe over scrollable card content navigates exactly once`() = runComposeUiTest {
        val scrollPage = TractPage(
            cards = { p ->
                listOf(
                    TractPage.Card(
                        p,
                        0,
                        label = { t -> Text(t, text = "Card 0") },
                        content = { c -> (0..49).map { Text(c, text = "Paragraph $it of a long scrollable card") } },
                    ),
                    TractPage.Card(p, 1, label = { t -> Text(t, text = "Card 1") }),
                    TractPage.Card(p, 2, label = { t -> Text(t, text = "Card 2") }),
                )
            },
        )
        val pageState = TractPageState(scrollPage).apply { navigateToCard(scrollPage.cards[0]) }
        val nextText = getString(Res.string.tract_card_action_next)
        var swiped = 0
        setTestContent(pageState, onSwipe = { swiped++ })

        // swipeUp()'s default start/end sit on the card surface's own top/bottom edges, which would land on
        // the header or the nav row rather than the scrollable content; pick explicit points confined to the
        // content region (below the header/divider, above the nav row) so the gesture actually starts inside
        // RenderTractCardContent's verticalScroll and is routed through the NestedScrollConnection path.
        val surfaceBounds = cardSurface(0).getBoundsInRoot()
        val headerBottom = onNodeWithText("Card 0").getBoundsInRoot().bottom - surfaceBounds.top
        val navTop = onNode(hasText(nextText) and hasAnyAncestor(hasTestTag("card-0")))
            .getBoundsInRoot().top - surfaceBounds.top
        assertTrue(
            navTop > headerBottom,
            "expected a non-empty scrollable content region between the header and the nav row",
        )
        val startYLocal = headerBottom + (navTop - headerBottom) * 0.7f
        val endYLocal = headerBottom + (navTop - headerBottom) * 0.3f
        assertTrue(startYLocal > headerBottom, "swipe start should be below the header/divider, was $startYLocal")

        cardSurface(0).performTouchInput {
            val x = width / 2f
            swipe(start = Offset(x, startYLocal.toPx()), end = Offset(x, endYLocal.toPx()), durationMillis = 50)
        }
        waitForIdle()
        assertEquals(scrollPage.cards[1], pageState.activeCard, "swipe over scrollable content should still navigate")
        assertEquals(1, swiped, "onCardSwiped should fire exactly once, not once per gesture detector")
    }
    // endregion Action - Fling

    // region Animation - Card Change
    @Test
    fun `Animation - card change - positions animate between states`() = runComposeUiTest {
        mainClock.autoAdvance = false
        val pageState = TractPageState(page)
        setTestContent(pageState)

        val container = onNodeWithTag("container").getBoundsInRoot()
        val stackedTop = cardSurface(0).getBoundsInRoot().top

        pageState.navigateToCard(page.cards[0])
        advanceTimeBy(192)
        val midTop = cardSurface(0).getBoundsInRoot().top
        advanceTimeBy(2_000)
        val settledTop = cardSurface(0).getBoundsInRoot().top

        assertTrue(
            midTop < stackedTop && midTop > settledTop,
            "card should be strictly between its stacked and active positions mid-animation, was $midTop",
        )
        assertEquals(container.top + 16.dp, settledTop, "card should settle at the active position")
    }

    @Test
    fun `Animation - card change - call to action fades in after the card change completes`() = runComposeUiTest {
        mainClock.autoAdvance = false
        val pageState = TractPageState(page)
        setTestContent(pageState)

        pageState.navigateToCard(page.cards[2])
        advanceTimeBy(96)
        onNodeWithTag("cta").assertIsNotDisplayed()

        advanceTimeBy(960)
        onNodeWithTag("cta").assertIsDisplayed()
    }

    @Test
    fun `Animation - card change - retargets when the active card changes mid-animation`() = runComposeUiTest {
        mainClock.autoAdvance = false
        val pageState = TractPageState(page)
        setTestContent(pageState)

        val container = onNodeWithTag("container").getBoundsInRoot()
        val card1StackedTop = cardSurface(1).getBoundsInRoot().top

        pageState.navigateToCard(page.cards[0])
        advanceTimeBy(96)
        val card1BeforeRetarget = cardSurface(1).getBoundsInRoot().top
        assertTrue(
            card1BeforeRetarget > card1StackedTop,
            "card 1 should be mid-flight down toward its peek position before retargeting, was $card1BeforeRetarget",
        )

        pageState.navigateToCard(page.cards[1])
        advanceTimeBy(96)
        val card1AfterRetarget = cardSurface(1).getBoundsInRoot().top
        assertTrue(
            card1AfterRetarget < card1BeforeRetarget,
            "card 1 should reverse toward its new target immediately after retargeting, was $card1AfterRetarget",
        )

        // advance well past both animations' 300ms duration
        advanceTimeBy(2_000)

        val activeTop = cardSurface(1).getBoundsInRoot().top
        assertEquals(container.top + 16.dp, activeTop, "retargeted card should settle at the active position")
    }
    // endregion Animation - Card Change

    // region Animation - Card Bounce
    @Test
    fun `Animation - Card Bounce - disabling mid-bounce completes the running bounce`() = runComposeUiTest {
        mainClock.autoAdvance = false
        val pageState = TractPageState(page)
        setTestContent(pageState)
        val baseTop = cardSurface(0).getBoundsInRoot().top

        pageState.isBounceFirstCard = true
        advanceTimeBy(2_240)
        val bounceTop = cardSurface(0).getBoundsInRoot().top
        assertTrue(bounceTop < baseTop, "card should be lifted above its base position mid-bounce, was $bounceTop")

        pageState.isBounceFirstCard = false
        advanceTimeBy(80)
        val animatingTop = cardSurface(0).getBoundsInRoot().top
        assertTrue(
            animatingTop != baseTop,
            "the running bounce should keep animating after the hint is disabled",
        )

        // run past the end of the ~1s bounce animation
        advanceTimeBy(960)
        assertEquals(
            baseTop,
            cardSurface(0).getBoundsInRoot().top,
            "the bounce should finish at the card's base position"
        )

        // run 60 seconds past the animation ending to ensure the bounce doesn't trigger again
        val endTime = mainClock.currentTime + 60_000
        while (mainClock.currentTime < endTime) {
            mainClock.advanceTimeByFrame()
            assertEquals(
                baseTop,
                cardSurface(0).getBoundsInRoot().top,
                "no further bounces should run after the hint is disabled"
            )
        }
    }

    @Test
    fun `Animation - Card Bounce - navigating to a card immediately interrupts the bounce`() = runComposeUiTest {
        mainClock.autoAdvance = false
        val pageState = TractPageState(page)
        setTestContent(pageState)
        val container = onNodeWithTag("container").getBoundsInRoot()
        val baseTop = cardSurface(0).getBoundsInRoot().top

        pageState.isBounceFirstCard = true
        advanceTimeBy(2_240)
        val bounceTop = cardSurface(0).getBoundsInRoot().top
        assertTrue(bounceTop < baseTop, "card should be lifted above its base position mid-bounce, was $bounceTop")

        pageState.navigateToCard(page.cards[0])
        // the bounce never lifts the card more than 40dp above its base position, so clearing that range within a
        // few frames proves the card-change animation took over immediately instead of the bounce finishing first
        advanceTimeBy(128)
        val interruptedTop = cardSurface(0).getBoundsInRoot().top
        assertTrue(
            interruptedTop < baseTop - 40.dp,
            "card should move toward the active position immediately, was $interruptedTop",
        )

        // run past the 300ms card-change animation
        advanceTimeBy(400)
        assertEquals(
            container.top + 16.dp,
            cardSurface(0).getBoundsInRoot().top,
            "card should settle at the active position"
        )
    }
    // endregion Animation - Card Bounce

    @Test
    fun `UI - offscreen cards - not placed`() = runComposeUiTest {
        val tallPage = TractPage(
            cards = { p -> (0..9).map { TractPage.Card(p, it, label = { t -> Text(t, text = "Card $it") }) } },
        )
        val pageState = TractPageState(tallPage).apply { navigateToCard(tallPage.cards[5]) }
        setTestContent(pageState)

        onNodeWithTag("hero").assertIsNotDisplayed()
        (0..4).forEach { onNodeWithTag("card-$it").assertIsNotDisplayed() }
        (7..9).forEach { onNodeWithTag("card-$it").assertIsNotDisplayed() }
        onNodeWithTag("card-5").assertIsDisplayed()
        onNodeWithTag("card-6").assertIsDisplayed()
    }
}
