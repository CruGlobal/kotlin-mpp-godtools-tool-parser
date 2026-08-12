package org.cru.godtools.shared.renderer.tract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@Composable
fun rememberTractPageState(page: TractPage) =
    rememberSaveable(saver = TractPageState.Saver(page)) { TractPageState(page) }
        .apply { updatePage(page) }

@Stable
class TractPageState internal constructor(
    page: TractPage,
    initialActiveCardId: String?,
    enabledHiddenCards: Collection<String>,
) {
    constructor(page: TractPage) : this(page, initialActiveCardId = null, enabledHiddenCards = emptySet())

    var page: TractPage by mutableStateOf(page)
        private set

    internal val enabledHiddenCards = mutableStateSetOf(*enabledHiddenCards.toTypedArray())
    val visibleCards by derivedStateOf {
        page.cards.filter { !it.isHidden || it.id in this.enabledHiddenCards }
    }

    private var activeCardId: String? by mutableStateOf(initialActiveCardId)
    val activeCard: TractPage.Card? by derivedStateOf { page.cards.firstOrNull { it.id == activeCardId } }
    val activeCardPosition get() = visibleCards.indexOf(activeCard)

    /** Host-settable: enables the periodic first-card bounce hint while no card is active. */
    var isBounceFirstCard by mutableStateOf(false)

    fun navigateToCard(card: TractPage.Card?) {
        require(card == null || card.page == page) { "card must belong to this state's page" }
        if (card?.isHidden == true) enabledHiddenCards += card.id
        activeCardId = card?.id
        hideInactiveHiddenCards()
    }

    internal fun nextCard(): Boolean {
        val next = visibleCards.getOrNull(activeCardPosition + 1) ?: return false
        navigateToCard(next)
        return true
    }

    internal fun previousCard(): Boolean {
        if (activeCardPosition < 0) return false
        navigateToCard(visibleCards.getOrNull(activeCardPosition - 1))
        return true
    }

    internal fun dismissActiveCard() = navigateToCard(null)

    fun updatePage(page: TractPage) {
        this.page = page
        if (activeCardId != null && page.cards.none { it.id == activeCardId }) navigateToCard(null)
    }

    private fun hideInactiveHiddenCards() {
        enabledHiddenCards.removeAll { it != activeCardId }
    }

    companion object {
        @Suppress("FunctionName")
        fun Saver(page: TractPage) = listSaver<TractPageState, Any?>(
            save = { listOf(it.activeCardId, ArrayList(it.enabledHiddenCards)) },
            restore = {
                @Suppress("UNCHECKED_CAST")
                TractPageState(page, initialActiveCardId = it[0] as String?, enabledHiddenCards = it[1] as List<String>)
            },
        )
    }
}
