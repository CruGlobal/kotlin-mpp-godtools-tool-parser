package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.flow.drop
import org.cru.godtools.shared.renderer.RenderBackground
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.tips.TipDownArrow
import org.cru.godtools.shared.renderer.util.ContentEventListener
import org.cru.godtools.shared.renderer.util.ProvideLayoutDirectionFromLocale
import org.cru.godtools.shared.renderer.util.ProvideResumedLifecycleOwner
import org.cru.godtools.shared.renderer.util.triggerScreenView
import org.cru.godtools.shared.tool.analytics.ToolAnalyticsScreenNames
import org.cru.godtools.shared.tool.parser.model.tract.Modal
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@Composable
fun RenderTractPage(
    page: TractPage,
    modifier: Modifier = Modifier,
    contentInsets: PaddingValues = PaddingValues(0.dp),
    state: State = remember { State() },
    pageState: TractPageState = rememberTractPageState(page),
    pageEvents: (TractPageEvent) -> Unit = {},
) = ProvideLayoutDirectionFromLocale(page.manifest.locale) {
    val currentPageEvents by rememberUpdatedState(pageEvents)

    // content event wiring: dismiss/reveal cards and surface modal-open requests to the host. Dismiss is
    // checked against the original active card before reveal can replace it with a newly revealed card.
    ContentEventListener(state, page, pageState) { event ->
        if (pageState.activeCard?.dismissListeners?.contains(event) == true) pageState.dismissActiveCard()
        page.cards.firstOrNull { event in it.listeners }?.let { pageState.navigateToCard(it) }
        page.modals.firstOrNull { event in it.listeners }
            ?.let { currentPageEvents(TractPageEvent.OpenModal(it)) }
    }

    // surface active card changes to the host (live share, host analytics)
    LaunchedEffect(pageState) {
        snapshotFlow { pageState.activeCard }
            .drop(1)
            .collect { currentPageEvents(TractPageEvent.ActiveCardChanged(it)) }
    }

    Box(modifier) {
        RenderBackground(page.background, Modifier.matchParentSize())

        val showTips by state.showTips.collectAsState()
        TractPageLayout(
            pageState = pageState,
            hero = {
                val isResumed by remember(pageState) { derivedStateOf { pageState.activeCard == null } }
                ProvideResumedLifecycleOwner(resumed = isResumed) {
                    LifecycleResumeEffect(page, state) {
                        state.triggerScreenView(page.manifest, ToolAnalyticsScreenNames.forTractPage(page))
                        onPauseOrDispose { }
                    }
                    RenderTractHero(
                        page,
                        state = state,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }
            },
            card = { card ->
                val isResumed by remember(pageState, card) { derivedStateOf { pageState.activeCard == card } }
                ProvideResumedLifecycleOwner(resumed = isResumed) {
                    RenderTractCard(
                        card,
                        state = state,
                        onToggleCard = {
                            currentPageEvents(TractPageEvent.CardTapped)
                            pageState.navigateToCard(card.takeIf { it != pageState.activeCard })
                        },
                        onPreviousCard = { pageState.previousCard() },
                        onNextCard = { pageState.nextCard() },
                    )
                }
            },
            callToAction = {
                RenderTractCallToAction(page, onNextPage = { currentPageEvents(TractPageEvent.GoToNextPage) })
            },
            callToActionTip = page.callToAction.tip
                ?.takeIf { showTips }
                ?.let { tip -> { TipDownArrow(tip, state) } },
            onCardSwipe = { currentPageEvents(TractPageEvent.CardSwiped) },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentInsets)
        )
    }
}

sealed interface TractPageEvent {
    data class ActiveCardChanged(val card: TractPage.Card?) : TractPageEvent
    data class OpenModal(val modal: Modal) : TractPageEvent
    data object GoToNextPage : TractPageEvent
    data object CardTapped : TractPageEvent
    data object CardSwiped : TractPageEvent
}
