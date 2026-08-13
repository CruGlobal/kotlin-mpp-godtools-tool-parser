package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

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
                    onCardSwiped = onSwipe,
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

    @Test
    fun `UI - call to action - shown at the bottom edge when last card is active`() = runComposeUiTest {
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[2]) }
        setTestContent(pageState)

        val container = onNodeWithTag("container").getBoundsInRoot()
        val ctaBounds = onNodeWithTag("cta").assertIsDisplayed().getBoundsInRoot()
        assertEquals(container.bottom, ctaBounds.bottom, "CTA should sit at the bottom edge")
    }

    @Test
    fun `UI - offscreen cards - not placed`() = runComposeUiTest {
        // a taller stack (more cards than fit) with a middle card active: exercises the hero, the pre-active
        // cards, and the cards further out than the peeking card all being pushed fully off the viewport
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
