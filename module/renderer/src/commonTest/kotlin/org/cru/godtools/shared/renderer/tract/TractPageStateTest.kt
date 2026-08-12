package org.cru.godtools.shared.renderer.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

class TractPageStateTest {
    private val page = TractPage(
        cards = { page ->
            listOf(
                TractPage.Card(page, 0),
                TractPage.Card(page, 1, isHidden = true),
                TractPage.Card(page, 2),
            )
        },
    )

    @Test
    fun `State - visibleCards - excludes hidden cards by default`() {
        val state = TractPageState(page)
        assertEquals(listOf(page.cards[0], page.cards[2]), state.visibleCards)
    }

    @Test
    fun `Action - navigateToCard - enables hidden card`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])
        assertEquals(page.cards[1], state.activeCard)
        assertEquals(page.cards, state.visibleCards)
        assertEquals(1, state.activeCardPosition)
    }

    @Test
    fun `Action - navigateToCard - navigating away from hidden card re-hides it`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])
        state.navigateToCard(page.cards[0])
        assertEquals(page.cards[0], state.activeCard)
        assertEquals(listOf(page.cards[0], page.cards[2]), state.visibleCards)
    }

    @Test
    fun `Action - nextCard and previousCard - navigate within visible cards`() {
        val state = TractPageState(page)
        assertTrue(state.nextCard())
        assertEquals(page.cards[0], state.activeCard)
        assertTrue(state.nextCard())
        assertEquals(page.cards[2], state.activeCard)
        assertFalse(state.nextCard())
        assertTrue(state.previousCard())
        assertEquals(page.cards[0], state.activeCard)
        assertTrue(state.previousCard())
        assertNull(state.activeCard)
        assertEquals(-1, state.activeCardPosition)
        assertFalse(state.previousCard())
    }

    @Test
    fun `Action - dismissActiveCard - returns to hero`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[0])
        state.dismissActiveCard()
        assertNull(state.activeCard)
    }

    @Test
    fun `State - Saver - round trip preserves activeCard and enabled hidden cards`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])

        val saver = TractPageState.Saver(page)
        val saved = with(saver) { TestSaverScope.save(state) }!!
        val restored = saver.restore(saved)!!

        assertEquals(page.cards[1], restored.activeCard)
        assertEquals(page.cards, restored.visibleCards)
    }

    @Test
    fun `Action - updatePage - preserves active card and enabled hidden cards across equivalent pages`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])

        val equivalentPage = TractPage(
            cards = { p ->
                listOf(
                    TractPage.Card(p, 0),
                    TractPage.Card(p, 1, isHidden = true),
                    TractPage.Card(p, 2),
                )
            },
        )

        state.updatePage(equivalentPage)

        assertEquals(equivalentPage, state.page, "state page is updated")
        assertNotNull(state.activeCard, "activeCard is resolved in the new equivalent page")
        assertEquals(
            equivalentPage.cards[1].id,
            state.activeCard?.id,
            "the resolved card is from the new page",
        )
        assertEquals(3, state.visibleCards.size, "enabled hidden cards preserve visibility")
    }

    @Test
    fun `Action - updatePage - same page is a no-op`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])

        val originalActiveCard = state.activeCard
        val originalVisibleCards = state.visibleCards

        state.updatePage(page)

        assertEquals(originalActiveCard, state.activeCard)
        assertEquals(originalVisibleCards, state.visibleCards)
    }

    @Test
    fun `Action - updatePage - page missing active card resets to hero permanently`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])

        val pageMissingCard = TractPage(
            cards = { p ->
                listOf(
                    TractPage.Card(p, 0),
                    TractPage.Card(p, 2),
                )
            },
        )

        state.updatePage(pageMissingCard)
        assertNull(
            state.activeCard,
            "switching to a page without the active card resets to the hero",
        )

        state.updatePage(page)
        assertNull(
            state.activeCard,
            "the reset is permanent: switching back does not restore the card",
        )
        assertEquals(
            listOf(page.cards[0], page.cards[2]),
            state.visibleCards,
            "a revealed hidden card re-hides when the reset occurs",
        )
    }

    private object TestSaverScope : androidx.compose.runtime.saveable.SaverScope {
        override fun canBeSaved(value: Any) = true
    }
}
