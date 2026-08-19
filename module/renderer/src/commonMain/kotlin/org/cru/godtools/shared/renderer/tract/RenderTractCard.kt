package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.ccci.gto.android.common.compose.ui.draw.invisibleIf
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.content.extensions.painterTip
import org.cru.godtools.shared.renderer.content.extensions.triggerAnalyticsEvents
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_next
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_previous
import org.cru.godtools.shared.renderer.generated.resources.tract_card_position
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.tips.TipIconSize
import org.cru.godtools.shared.renderer.util.triggerScreenView
import org.cru.godtools.shared.tool.analytics.ToolAnalyticsScreenNames
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.backgroundColor
import org.jetbrains.compose.resources.stringResource

private const val SlotHeader = "header"
private const val SlotContent = "content"
private const val SlotNav = "nav"

// Alignment lines published by RenderTractCard, measured from the card child's top edge.
internal val TractCardPaddingLine = HorizontalAlignmentLine(::minOf)
internal val TractCardPeekLine = HorizontalAlignmentLine(::minOf)
internal val TractCardStackLine = HorizontalAlignmentLine(::minOf)

private val CardLabelPaddingTop = 16.dp
private val CardDividerThickness = 1.dp

internal const val TestTagTractCard = "tract_card"
internal const val TestTagTractCardTipIndicator = "tract_card_tip_indicator"

@Composable
internal fun RenderTractCard(
    card: TractPage.Card,
    state: State,
    onToggleCard: () -> Unit,
    onPreviousCard: () -> Unit,
    onNextCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    LifecycleResumeEffect(card, state) {
        state.triggerScreenView(card.manifest, ToolAnalyticsScreenNames.forTractPage(card.page, card))
        val events = card.triggerAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE, state, coroutineScope)
        onPauseOrDispose {
            events.forEach { it.cancel() }
            card.triggerAnalyticsEvents(AnalyticsEvent.Trigger.HIDDEN, state, coroutineScope)
        }
    }

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = card.backgroundColor.toComposeColor()),
        elevation = ToolTheme.cardElevation(),
        modifier = modifier
            .padding(16.dp)
            .testTag(TestTagTractCard)
    ) {
        Layout(
            content = {
                RenderTractCardHeader(card, state, onToggleCard, Modifier.layoutId(SlotHeader))
                RenderTractCardContent(
                    card,
                    state = state,
                    modifier = Modifier
                        .layoutId(SlotContent)
                        .padding(top = 4.dp)
                )
                RenderTractCardBottomNav(card, onPreviousCard, onNextCard, Modifier.layoutId(SlotNav))
            },
        ) { measurables, constraints ->
            val loose = constraints.copy(minWidth = constraints.maxWidth, minHeight = 0)
            val header = measurables.first { it.layoutId == SlotHeader }.measure(loose)
            val nav = measurables.first { it.layoutId == SlotNav }.measure(loose)
            val content = measurables.first { it.layoutId == SlotContent }.measure(
                loose.copy(maxHeight = (constraints.maxHeight - header.height - nav.height).coerceAtLeast(0))
            )

            val height = constraints.maxHeight
            layout(
                constraints.maxWidth,
                height,
                alignmentLines = mapOf(
                    TractCardPaddingLine to 0,
                    TractCardPeekLine to CardLabelPaddingTop.roundToPx(),
                    TractCardStackLine to header.height - CardDividerThickness.roundToPx(),
                ),
            ) {
                header.place(0, 0)
                content.place(0, header.height)
                nav.place(0, height - nav.height)
            }
        }
    }
}

@Composable
private fun RenderTractCardHeader(
    card: TractPage.Card,
    state: State,
    onToggleCard: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier.clickable(onClick = onToggleCard)) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ContentHorizontalPadding)
            .padding(top = CardLabelPaddingTop, bottom = 12.dp)
    ) {
        card.label?.let {
            ProvideTextStyle(ToolTheme.TractCardLabelTextStyle) {
                RenderTextNode(
                    it,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        val indicatorTip = card.tips.firstOrNull()
            ?: card.page.callToAction.tip.takeIf { card.isLastVisibleCard }
        if (indicatorTip != null && state.showTips.collectAsState().value) {
            Image(
                painterTip(indicatorTip, isComplete = false),
                contentDescription = null,
                modifier = Modifier
                    .size(TipIconSize)
                    .testTag(TestTagTractCardTipIndicator)
            )
        }
    }
    HorizontalDivider(
        thickness = CardDividerThickness,
        color = card.textColor.toComposeColor(),
        modifier = Modifier.padding(horizontal = ContentHorizontalPadding)
    )
}

@Composable
private fun RenderTractCardBottomNav(
    card: TractPage.Card,
    onPreviousCard: () -> Unit,
    onNextCard: () -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
        .padding(horizontal = ContentHorizontalPadding)
        .fillMaxWidth()
        .invisibleIf(card.isHidden)
) {
    val visiblePosition = card.visiblePosition
    val visibleCards = card.page.visibleCards.size

    val buttonColors = ButtonDefaults.textButtonColors(contentColor = card.textColor.toComposeColor())
    val previousHidden = card.isHidden || visiblePosition == null || visiblePosition == 0
    val nextHidden = card.isHidden || visiblePosition == null || visiblePosition + 1 >= visibleCards

    Box(Modifier.weight(1f)) {
        TextButton(
            onClick = onPreviousCard,
            enabled = !previousHidden,
            colors = buttonColors,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .invisibleIf(previousHidden)
        ) { Text(stringResource(Res.string.tract_card_action_previous)) }
    }
    Text(
        stringResource(Res.string.tract_card_position, (visiblePosition ?: 0) + 1, visibleCards),
        color = card.textColor.toComposeColor(),
    )
    Box(Modifier.weight(1f)) {
        TextButton(
            onClick = onNextCard,
            enabled = !nextHidden,
            colors = buttonColors,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .invisibleIf(nextHidden)
        ) { Text(stringResource(Res.string.tract_card_action_next)) }
    }
}
