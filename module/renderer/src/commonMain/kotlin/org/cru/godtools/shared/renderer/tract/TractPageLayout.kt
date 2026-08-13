package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

internal sealed interface TractPageLayoutSlot {
    data object Hero : TractPageLayoutSlot
    data object CallToAction : TractPageLayoutSlot
    data object CallToActionTip : TractPageLayoutSlot
    data class Card(val id: String) : TractPageLayoutSlot
}

/**
 * Stacked-card page layout: hero on the bottom of the stack, cards sliding up over it one at a time, and a
 * call-to-action pinned to the bottom edge. Port of Android's `PageContentLayout` measure/placement semantics.
 *
 * This task only snaps children to their target positions for the current [pageState]; [onCardSwiped] is wired
 * for a later task that layers animation and gesture handling on top of this measure pass.
 */
@Suppress("ktlint:compose:parameter-naming") // onCardSwiped mirrors the CardSwiped event name (Task 8/9 contract)
@Composable
internal fun TractPageLayout(
    pageState: TractPageState,
    hero: @Composable () -> Unit,
    callToAction: @Composable () -> Unit,
    card: @Composable (TractPage.Card) -> Unit,
    modifier: Modifier = Modifier,
    callToActionTip: (@Composable () -> Unit)? = null,
    onCardSwiped: () -> Unit = {},
) {
    val visibleCards = pageState.visibleCards

    Layout(
        content = {
            Box(Modifier.layoutId(TractPageLayoutSlot.Hero)) { hero() }
            visibleCards.forEach { c ->
                key(c.id) { Box(Modifier.layoutId(TractPageLayoutSlot.Card(c.id))) { card(c) } }
            }
            Box(Modifier.layoutId(TractPageLayoutSlot.CallToAction)) { callToAction() }
            if (callToActionTip != null) {
                Box(Modifier.layoutId(TractPageLayoutSlot.CallToActionTip)) { callToActionTip() }
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val loose = constraints.copy(minWidth = 0, minHeight = 0)

        // 1. measure the call to action first: its height reduces card/hero space
        val cta = measurables.firstOrNull { it.layoutId == TractPageLayoutSlot.CallToAction }?.measure(loose)
        val ctaTip = measurables.firstOrNull { it.layoutId == TractPageLayoutSlot.CallToActionTip }?.measure(loose)
        val ctaHeight = cta?.height ?: 0

        // 2. measure cards backwards, accumulating the stacked label heights
        val cardMeasurables = visibleCards.map { c ->
            measurables.first { it.layoutId == TractPageLayoutSlot.Card(c.id) }
        }
        val cardPlaceables = arrayOfNulls<Placeable>(cardMeasurables.size)
        val peekLine = IntArray(cardMeasurables.size)
        val stackLine = IntArray(cardMeasurables.size)
        val siblingStackOffset = IntArray(cardMeasurables.size)
        var nextCardPeek = 0
        var cardStackHeight = 0
        var firstCardPadding = 0
        for (i in cardMeasurables.indices.reversed()) {
            val heightUsed = maxOf(nextCardPeek, ctaHeight)
            val placeable = cardMeasurables[i]
                .measure(loose.copy(maxHeight = (height - heightUsed).coerceAtLeast(0)))
            val padding = placeable[TractCardPaddingLine].orZero()
            peekLine[i] = placeable[TractCardPeekLine].orZero()
            stackLine[i] = placeable[TractCardStackLine].orZero()
            siblingStackOffset[i] = cardStackHeight
            cardStackHeight += stackLine[i] - padding
            nextCardPeek = peekLine[i]
            firstCardPadding = padding
            cardPlaceables[i] = placeable
        }

        // 3. measure the hero with the space used by the card stack (or the CTA when there are no cards)
        val heroHeightUsed = if (cardMeasurables.isNotEmpty()) cardStackHeight + firstCardPadding else ctaHeight
        val heroPlaceable = measurables.firstOrNull { it.layoutId == TractPageLayoutSlot.Hero }
            ?.measure(loose.copy(maxHeight = (height - heroHeightUsed).coerceAtLeast(0)))

        // 4. target positions for the current active card (port of PageContentLayout.getChildTargetY)
        val activePosition = pageState.activeCardPosition
        val ctaVisible = activePosition + 1 >= visibleCards.size

        layout(width, height) {
            // fully offscreen children are skipped entirely rather than placed out of bounds
            heroPlaceable?.let {
                val y = if (activePosition < 0) 0 else -height
                if (y + it.height > 0 && y < height) it.placeRelative(0, y)
            }
            cardPlaceables.forEachIndexed { i, placeable ->
                placeable ?: return@forEachIndexed
                val y = when {
                    activePosition < 0 -> height - stackLine[i] - siblingStackOffset[i]
                    i < activePosition -> -height
                    i == activePosition -> 0
                    i == activePosition + 1 -> height - peekLine[i]
                    else -> height
                }
                if (y + placeable.height > 0 && y < height) placeable.placeRelative(0, y)
            }
            cta?.placeRelativeWithLayer(0, height - ctaHeight) { alpha = if (ctaVisible) 1f else 0f }
            if (ctaTip != null) {
                ctaTip.placeRelativeWithLayer(24.dp.roundToPx(), height - ctaHeight - ctaTip.height) {
                    alpha = if (ctaVisible) 1f else 0f
                }
            }
        }
    }
}

private fun Int.orZero() = takeIf { it != AlignmentLine.Unspecified } ?: 0
