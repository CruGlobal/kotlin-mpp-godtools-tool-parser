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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

internal sealed interface TractPageLayoutSlot {
    data object Hero : TractPageLayoutSlot
    data class Card(val id: String) : TractPageLayoutSlot
    data object CallToAction : TractPageLayoutSlot
    data object CallToActionTip : TractPageLayoutSlot
}

private val FlingVelocityThreshold = 1_000.dp

/**
 * Stacked-card page layout: hero on the bottom of the stack, cards sliding up over it one at a time, and a
 * call-to-action pinned to the bottom edge. Includes card-change animation, swipe-to-navigate gestures, and the
 * first-card bounce hint.
 */
@Composable
internal fun TractPageLayout(
    pageState: TractPageState,
    hero: @Composable () -> Unit,
    callToAction: @Composable () -> Unit,
    card: @Composable (TractPage.Card) -> Unit,
    modifier: Modifier = Modifier,
    callToActionTip: (@Composable () -> Unit)? = null,
    onCardSwipe: () -> Unit = {},
) {
    val visibleCards = pageState.visibleCards
    val animations = remember(pageState) { TractPageLayoutAnimations() }
    val onCardSwipe by rememberUpdatedState(onCardSwipe)

    if (pageState.isBounceFirstCard) BounceFirstCardEffect(pageState, animations)
    AnimateTargetsEffect(animations)

    // region Fling logic
    val flingThresholdPx = with(LocalDensity.current) { FlingVelocityThreshold.toPx() }
    val nestedScrollConnection = remember(pageState, flingThresholdPx) {
        object : NestedScrollConnection {
            override suspend fun onPreFling(available: Velocity) =
                if (pageState.handleFling(available.y, flingThresholdPx, onCardSwipe)) available else Velocity.Zero
        }
    }
    // endregion Fling logic

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
                val velocityTracker = VelocityTracker()
                var ignore = false
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        velocityTracker.resetTracking()
                        val gutter = minOf(16.dp.toPx(), size.height / 10f)
                        ignore = offset.y > size.height - gutter
                    },
                    onVerticalDrag = { change, _ -> velocityTracker.addPosition(change.uptimeMillis, change.position) },
                    onDragEnd = {
                        if (!ignore) {
                            pageState.handleFling(
                                velocityY = velocityTracker.calculateVelocity().y,
                                thresholdPx = FlingVelocityThreshold.toPx(),
                                onCardSwipe = onCardSwipe,
                            )
                        }
                    },
                )
            }
    ) { measurables, constraints ->
        val viewportWidth = constraints.maxWidth
        val viewportHeight = constraints.maxHeight
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val measurablesBySlot = measurables.associateBy { it.layoutId }

        // region Measure CTA & CTA Tip
        val cta = measurablesBySlot[TractPageLayoutSlot.CallToAction]?.measure(loose)
        val ctaTip = measurablesBySlot[TractPageLayoutSlot.CallToActionTip]?.measure(loose)
        val ctaHeight = cta?.height ?: 0
        val ctaTipOffsetPx = 24.dp.roundToPx()
        // endregion Measure CTA & CTA Tip

        // region Measure Cards
        val cardPlaceables = arrayOfNulls<Placeable>(visibleCards.size)
        val cardPeekLine = IntArray(visibleCards.size)
        val cardStackLine = IntArray(visibleCards.size)
        val cardStackOffset = IntArray(visibleCards.size)

        var cardStackHeight = 0
        for (i in visibleCards.indices.reversed()) {
            val heightUsed = maxOf(cardPeekLine.getOrElse(i + 1) { 0 }, ctaHeight)
            val placeable = measurablesBySlot.getValue(TractPageLayoutSlot.Card(visibleCards[i].id))
                .measure(loose.copy(maxHeight = (viewportHeight - heightUsed).coerceAtLeast(0)))

            val padding = placeable.getOrElse(TractCardPaddingLine, 0)
            cardPeekLine[i] = placeable.getOrElse(TractCardPeekLine, 0)
            cardStackLine[i] = placeable.getOrElse(TractCardStackLine, 0)
            cardStackOffset[i] = cardStackHeight
            cardStackHeight += cardStackLine[i] - padding

            cardPlaceables[i] = placeable
        }
        val firstCardPadding = cardPlaceables.getOrNull(0)?.getOrElse(TractCardPaddingLine, 0) ?: 0
        // endregion Measure Cards

        // region Measure Hero
        val heroHeightUsed = if (visibleCards.isNotEmpty()) cardStackHeight + firstCardPadding else ctaHeight
        val heroPlaceable = measurablesBySlot[TractPageLayoutSlot.Hero]
            ?.measure(loose.copy(maxHeight = (viewportHeight - heroHeightUsed).coerceAtLeast(0)))
        // endregion Measure Hero

        // region Calculate Animation Targets
        val activePosition = pageState.activeCardPosition
        val ctaVisible = activePosition + 1 >= visibleCards.size
        val targetOffsets = buildMap {
            heroPlaceable?.let {
                put(TractPageLayoutSlot.Hero, if (activePosition < 0) 0f else -viewportHeight.toFloat())
            }
            visibleCards.forEachIndexed { i, c ->
                val y = when {
                    activePosition < 0 -> viewportHeight - cardStackLine[i] - cardStackOffset[i]
                    i < activePosition -> -viewportHeight
                    i == activePosition -> 0
                    i == activePosition + 1 -> viewportHeight - cardPeekLine[i]
                    else -> viewportHeight
                }
                put(TractPageLayoutSlot.Card(c.id), y.toFloat())
            }
        }
        animations.targets = TractPageLayoutAnimations.Targets(pageState.activeCard?.id, targetOffsets, ctaVisible)
        animations.offsets.keys.retainAll { it in targetOffsets.keys }

        val heroOffset = heroPlaceable?.let {
            animations.offset(TractPageLayoutSlot.Hero, targetOffsets.getValue(TractPageLayoutSlot.Hero))
        }
        val cardOffsets = Array(cardPlaceables.size) { i ->
            val slot = TractPageLayoutSlot.Card(visibleCards[i].id)
            animations.offset(slot, targetOffsets.getValue(slot))
        }
        val ctaAlpha = animations.ctaAlpha(if (ctaVisible) 1f else 0f)
        // endregion Calculate Animation Targets

        layout(viewportWidth, viewportHeight) {
            fun Placeable.placeIfVisible(y: Int) {
                if (y + height > 0 && y < viewportHeight) placeRelative(0, y)
            }

            if (heroPlaceable != null && heroOffset != null) {
                heroPlaceable.placeIfVisible(heroOffset.value.roundToInt())
            }

            cardPlaceables.forEachIndexed { i, placeable ->
                placeable?.placeIfVisible(cardOffsets[i].value.roundToInt())
            }

            val alpha = ctaAlpha.value
            if (alpha > 0f) {
                cta?.placeRelativeWithLayer(0, viewportHeight - ctaHeight) { this.alpha = alpha }
                ctaTip?.placeRelativeWithLayer(ctaTipOffsetPx, viewportHeight - ctaHeight - ctaTip.height) {
                    this.alpha = alpha
                }
            }
        }
    }
}

// region Animations
private val CardChangeAnimationSpec = tween<Float>(durationMillis = 300, easing = LinearOutSlowInEasing)

@Stable
private class TractPageLayoutAnimations {
    val offsets = mutableMapOf<TractPageLayoutSlot, Animatable<Float, AnimationVector1D>>()
    private var ctaAlpha: Animatable<Float, AnimationVector1D>? = null
    var targets by mutableStateOf<Targets?>(null)

    data class Targets(
        val activeCardId: String?,
        val offsets: Map<TractPageLayoutSlot, Float>,
        val ctaVisible: Boolean,
    )

    fun offset(slot: TractPageLayoutSlot) = offsets[slot]
    fun offset(slot: TractPageLayoutSlot, initial: Float) = offsets.getOrPut(slot) { Animatable(initial) }

    fun ctaAlpha(initial: Float) = ctaAlpha ?: Animatable(initial).also { ctaAlpha = it }

    suspend fun animateToTargets(targets: Targets, animate: Boolean) {
        coroutineScope {
            targets.offsets.forEach { (slot, y) -> launch { offset(slot, y).animateOrSnapTo(y, animate) } }
            if (!targets.ctaVisible) launch { ctaAlpha(0f).animateOrSnapTo(0f, animate) }
        }

        // CTA fade-in is deferred until here; everything else (including CTA fade-out) already ran above
        if (targets.ctaVisible) ctaAlpha(1f).animateOrSnapTo(1f, animate)
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.animateOrSnapTo(target: Float, animate: Boolean) {
    when {
        animate -> animateTo(target, CardChangeAnimationSpec)
        else -> snapTo(target)
    }
}

@Composable
private fun AnimateTargetsEffect(animations: TractPageLayoutAnimations) {
    LaunchedEffect(animations) {
        var lastActiveCard: String? = null
        var initial = true
        snapshotFlow { animations.targets }.filterNotNull().collectLatest { targets ->
            val animate = !initial && targets.activeCardId != lastActiveCard
            initial = false
            lastActiveCard = targets.activeCardId
            animations.animateToTargets(targets, animate)
        }
    }
}

// region Bounce Effect
private val BounceInitialDelay = 2.seconds
private val BounceDelay = 7.seconds
private val BounceHeight = 40.dp
private const val BounceBounces = 4
private const val BounceDecay = 0.5
private val BounceFirstBounce = 400.milliseconds

@Composable
private fun BounceFirstCardEffect(pageState: TractPageState, animations: TractPageLayoutAnimations) {
    val bounceHeightPx = with(LocalDensity.current) { BounceHeight.toPx() }
    val bounceSpec = remember(bounceHeightPx) {
        TractBounceAnimationSpec(
            heightPx = bounceHeightPx,
            firstBounceDuration = BounceFirstBounce,
            bounces = BounceBounces,
            decay = BounceDecay,
        )
    }

    LaunchedEffect(pageState, animations, bounceSpec) {
        // flow of the firstCardId while there is no active card
        snapshotFlow { pageState.visibleCards.firstOrNull().takeIf { pageState.activeCard == null }?.id }
            .collectLatest { cardId ->
                if (cardId == null) return@collectLatest
                delay(BounceInitialDelay)

                while (true) {
                    val animatable = animations.offset(TractPageLayoutSlot.Card(cardId))
                    if (animatable != null && !animatable.isRunning) {
                        val baseY = animatable.value
                        try {
                            // NonCancellable so tearing down this coroutine (e.g. disabling the bounce) completes
                            // the running bounce instead of stopping it mid-air; other animations on this
                            // Animatable still interrupt it immediately through the MutatorMutex
                            withContext(NonCancellable) { animatable.animateTo(baseY, bounceSpec) }
                        } catch (_: CancellationException) {
                            ensureActive()
                        }
                    }

                    delay(BounceDelay)
                }
            }
    }
}
// endregion Bounce Effect
// endregion Animations

/** Navigate to the previous/next card when [velocityY] crosses [thresholdPx], reporting successful navigation. */
private fun TractPageState.handleFling(velocityY: Float, thresholdPx: Float, onCardSwipe: () -> Unit) = when {
    velocityY >= thresholdPx -> previousCard()
    velocityY <= -thresholdPx -> nextCard()
    else -> false
}.also { if (it) onCardSwipe() }

private fun Measured.getOrElse(alignmentLine: AlignmentLine, defaultValue: Int) =
    get(alignmentLine).takeIf { it != AlignmentLine.Unspecified } ?: defaultValue
