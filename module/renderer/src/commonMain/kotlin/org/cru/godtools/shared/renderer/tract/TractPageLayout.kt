package org.cru.godtools.shared.renderer.tract

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

internal sealed interface TractPageLayoutSlot {
    data object Hero : TractPageLayoutSlot
    data object CallToAction : TractPageLayoutSlot
    data object CallToActionTip : TractPageLayoutSlot
    data class Card(val id: String) : TractPageLayoutSlot
}

private val CardChangeAnimationSpec = tween<Float>(durationMillis = 300, easing = LinearOutSlowInEasing)
private const val BounceInitialDelayMs = 2_000L
private const val BounceDelayMs = 7_000L
private val BounceFirstBounce = 400.milliseconds
private val BounceHeight = 40.dp

// ≈ Android's ViewConfiguration minimum fling velocity, scaled up for a full-page swipe-to-navigate gesture
private val FlingVelocityThreshold = 1_000.dp

/** Per-slot animated Y offsets and CTA alpha, plus the latest targets published by the measure pass. */
@Stable
private class TractPageLayoutAnimations {
    val offsets = mutableMapOf<TractPageLayoutSlot, Animatable<Float, AnimationVector1D>>()
    private var ctaAlphaAnimatable: Animatable<Float, AnimationVector1D>? = null
    var targets by mutableStateOf<Targets?>(null)

    data class Targets(
        val activeCardId: String?,
        val offsets: Map<TractPageLayoutSlot, Float>,
        val ctaVisible: Boolean,
    )

    fun offset(slot: TractPageLayoutSlot, initial: Float) = offsets.getOrPut(slot) { Animatable(initial) }

    // lazily created at the correct initial alpha (like offset()), so the very first measure/placement pass
    // is already correct instead of relying on the LaunchedEffect's async snap to catch up a frame later
    fun ctaAlpha(initialVisible: Boolean) =
        ctaAlphaAnimatable ?: Animatable(if (initialVisible) 1f else 0f).also { ctaAlphaAnimatable = it }
}

/**
 * Stacked-card page layout: hero on the bottom of the stack, cards sliding up over it one at a time, and a
 * call-to-action pinned to the bottom edge. Port of Android's `PageContentLayout` measure/placement semantics,
 * including card-change animation, swipe-to-navigate gestures, and the first-card bounce hint.
 */
@Suppress("ktlint:compose:parameter-naming") // onCardSwiped names the callback for the CardSwiped event it
// reports; that name is part of this layout's public contract, not a naming-convention slip
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
    val animations = remember(pageState) { TractPageLayoutAnimations() }
    val currentOnCardSwiped by rememberUpdatedState(onCardSwiped)

    AnimateTargetsEffect(animations, pageState)
    if (pageState.isBounceFirstCard) BounceFirstCardEffect(animations, pageState)

    // swipe-to-navigate gestures: nested scroll for scrollable card content, raw drag flings elsewhere
    val flingThresholdPx = with(LocalDensity.current) { FlingVelocityThreshold.toPx() }
    fun handleFling(velocityY: Float): Boolean = when {
        velocityY >= flingThresholdPx && pageState.activeCardPosition >= 0 ->
            pageState.previousCard().also { if (it) currentOnCardSwiped() }

        velocityY <= -flingThresholdPx && pageState.activeCardPosition < pageState.visibleCards.size - 1 ->
            pageState.nextCard().also { if (it) currentOnCardSwiped() }

        else -> false
    }
    val nestedScrollConnection = remember(pageState) {
        object : NestedScrollConnection {
            override suspend fun onPreFling(available: Velocity) =
                if (handleFling(available.y)) available else Velocity.Zero
        }
    }

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
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .pointerInput(pageState) {
                // raw flings on non-scrollable areas; ignore gestures starting in the bottom gutter
                val velocityTracker = VelocityTracker()
                var ignore = false
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        velocityTracker.resetTracking()
                        val gutter = minOf(16.dp.toPx(), size.height / 10f)
                        ignore = offset.y > size.height - gutter
                    },
                    onVerticalDrag = { change, _ -> velocityTracker.addPosition(change.uptimeMillis, change.position) },
                    onDragEnd = { if (!ignore) handleFling(velocityTracker.calculateVelocity().y) },
                )
            },
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

        val targetOffsets = buildMap {
            heroPlaceable?.let { put(TractPageLayoutSlot.Hero, if (activePosition < 0) 0f else -height.toFloat()) }
            visibleCards.forEachIndexed { i, c ->
                val y = when {
                    activePosition < 0 -> height - stackLine[i] - siblingStackOffset[i]
                    i < activePosition -> -height
                    i == activePosition -> 0
                    i == activePosition + 1 -> height - peekLine[i]
                    else -> height
                }
                put(TractPageLayoutSlot.Card(c.id), y.toFloat())
            }
        }
        // structural equality guards this write so it cannot loop back into another measure pass
        val newTargets = TractPageLayoutAnimations.Targets(pageState.activeCard?.id, targetOffsets, ctaVisible)
        if (animations.targets != newTargets) animations.targets = newTargets
        animations.offsets.keys.retainAll { it in targetOffsets.keys }

        layout(width, height) {
            heroPlaceable?.let {
                val y = animations.offset(TractPageLayoutSlot.Hero, targetOffsets.getValue(TractPageLayoutSlot.Hero))
                    .value.roundToInt()
                if (y + it.height > 0 && y < height) it.placeRelative(0, y)
            }
            cardPlaceables.forEachIndexed { i, placeable ->
                placeable ?: return@forEachIndexed
                val slot = TractPageLayoutSlot.Card(visibleCards[i].id)
                val y = animations.offset(slot, targetOffsets.getValue(slot)).value.roundToInt()
                if (y + placeable.height > 0 && y < height) placeable.placeRelative(0, y)
            }
            val ctaAlpha = animations.ctaAlpha(ctaVisible).value
            if (ctaAlpha > 0f) {
                cta?.placeRelativeWithLayer(0, height - ctaHeight) { alpha = ctaAlpha }
                ctaTip?.placeRelativeWithLayer(24.dp.roundToPx(), height - ctaHeight - ctaTip.height) {
                    alpha = ctaAlpha
                }
            }
        }
    }
}

// animate (or snap, on first composition and on non-active-card-change target updates) toward new targets
@Composable
private fun AnimateTargetsEffect(animations: TractPageLayoutAnimations, pageState: TractPageState) {
    LaunchedEffect(animations) {
        var lastActiveCard: String? = pageState.activeCard?.id
        var initial = true
        snapshotFlow { animations.targets }.filterNotNull().collect { targets ->
            val animate = !initial && targets.activeCardId != lastActiveCard
            lastActiveCard = targets.activeCardId
            initial = false
            coroutineScope {
                targets.offsets.forEach { (slot, y) ->
                    launch {
                        val offset = animations.offset(slot, y)
                        if (animate) offset.animateTo(y, CardChangeAnimationSpec) else offset.snapTo(y)
                    }
                }
                // CTA fade-out runs concurrently with the offsets; fade-in is sequenced after them below
                if (!targets.ctaVisible) {
                    launch {
                        val ctaAlpha = animations.ctaAlpha(targets.ctaVisible)
                        if (animate) ctaAlpha.animateTo(0f, CardChangeAnimationSpec) else ctaAlpha.snapTo(0f)
                    }
                } else if (!animate) {
                    launch { animations.ctaAlpha(targets.ctaVisible).snapTo(1f) }
                }
            }
            if (targets.ctaVisible && animate) {
                animations.ctaAlpha(targets.ctaVisible).animateTo(1f, CardChangeAnimationSpec)
            }
        }
    }
}

// bounce hint: first card bounces after 2s (then every 7s) while no card is active and no other animation is
// running. Only composed while pageState.isBounceFirstCard is true (see the call site), so flipping that flag off
// disposes this effect; cancellation runs the NonCancellable finally below, snapping the card back to base rather
// than leaving it stranded mid-bounce.
@Composable
private fun BounceFirstCardEffect(animations: TractPageLayoutAnimations, pageState: TractPageState) {
    val bounceHeightPx = with(LocalDensity.current) { BounceHeight.toPx() }
    LaunchedEffect(animations, pageState) {
        snapshotFlow { pageState.activeCard == null && pageState.visibleCards.isNotEmpty() }
            .collectLatest { enabled ->
                if (!enabled) return@collectLatest
                val easing = TractBounceEasing()
                val bounceDuration = easing.totalDuration(BounceFirstBounce)
                delay(BounceInitialDelayMs)
                while (true) {
                    val firstCard = pageState.visibleCards.firstOrNull()
                    val offset = firstCard?.let { animations.offsets[TractPageLayoutSlot.Card(it.id)] }
                    if (offset != null && !offset.isRunning) {
                        val base = offset.value
                        // Android's animator always runs to completion; a coroutine bounce can be cancelled
                        // mid-frame (state change, composition leaving), so restore the base offset unconditionally
                        // rather than leaving the card stranded mid-air.
                        try {
                            val startNanos = withFrameNanos { it }
                            var fraction = 0f
                            while (fraction < 1f) {
                                val nowNanos = withFrameNanos { it }
                                fraction = ((nowNanos - startNanos).toFloat() / bounceDuration.inWholeNanoseconds)
                                    .coerceIn(0f, 1f)
                                offset.snapTo(base - bounceHeightPx * easing.transform(fraction))
                            }
                        } finally {
                            withContext(NonCancellable) { offset.snapTo(base) }
                        }
                    }
                    delay(BounceDelayMs)
                }
            }
    }
}

private fun Int.orZero() = takeIf { it != AlignmentLine.Unspecified } ?: 0
