package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.content.extensions.painterTip
import org.cru.godtools.shared.renderer.content.extensions.tipBackground
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip

private val InlineTipShape = RoundedCornerShape(6.dp)
private val InlineTipElevation = 4.dp
private val InlineTipSize = 40.dp
private val InlineTipIconSize = 24.dp

internal const val TestTagInlineTip = "inline_tip"

@Composable
internal fun RenderInlineTip(model: InlineTip, state: State) {
    if (!state.showTips.collectAsState().value) return

    val tip = model.tip ?: return
    val tipId = tip.id
    val isComplete by remember(state, tipId) { state.isTipCompleteFlow(tipId) }
        .collectAsState(state.isTipComplete(tipId))
    val isInvisible by remember(state, model) { model.isInvisibleFlow(state) }.collectAsState(model.isInvisible(state))

    Surface(
        onClick = { state.triggerEvent(State.Event.OpenTip(tipId)) },
        enabled = !isInvisible,
        shape = InlineTipShape,
        shadowElevation = InlineTipElevation,
        modifier = Modifier
            .visibility(model, state)
            .padding(horizontal = HorizontalPadding, vertical = 4.dp)
            .size(InlineTipSize)
            .testTag(TestTagInlineTip)
    ) {
        Image(
            painterTip(tip, isComplete = isComplete),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .tipBackground(isComplete = isComplete)
                .wrapContentSize()
                .size(InlineTipIconSize)
        )
    }
}
