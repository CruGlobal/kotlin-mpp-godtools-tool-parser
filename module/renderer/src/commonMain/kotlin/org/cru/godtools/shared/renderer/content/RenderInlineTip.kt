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
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.painterTip
import org.cru.godtools.shared.renderer.content.extensions.tipBackground
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.tips.TipCornerSize
import org.cru.godtools.shared.renderer.tips.TipElevation
import org.cru.godtools.shared.renderer.tips.TipIconSize
import org.cru.godtools.shared.renderer.tips.TipSize
import org.cru.godtools.shared.renderer.tips.produceIsComplete
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip

internal const val TestTagInlineTip = "inline_tip"

@Composable
internal fun RenderInlineTip(model: InlineTip, state: State) {
    if (!state.showTips.collectAsState().value) return

    val tip = model.tip ?: return
    val isComplete by tip.produceIsComplete()
    val isInvisible by remember(state, model) { model.isInvisibleFlow(state) }.collectAsState(model.isInvisible(state))

    Surface(
        onClick = { state.triggerEvent(State.Event.OpenTip(tip.id)) },
        enabled = !isInvisible,
        shape = RoundedCornerShape(TipCornerSize),
        shadowElevation = TipElevation,
        modifier = Modifier
            .visibility(model, state)
            .padding(horizontal = ContentHorizontalPadding, vertical = 4.dp)
            .size(TipSize)
            .testTag(TestTagInlineTip)
    ) {
        Image(
            painterTip(tip, isComplete = isComplete),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .tipBackground(isComplete = isComplete)
                .wrapContentSize()
                .size(TipIconSize)
        )
    }
}
