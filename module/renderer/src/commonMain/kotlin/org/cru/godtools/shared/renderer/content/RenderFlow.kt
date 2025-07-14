package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import org.cru.godtools.shared.renderer.content.extensions.arrangement
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.content.extensions.width
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Flow

internal const val TEST_TAG_FLOW = "flow"

@Composable
internal fun RenderFlow(flow: Flow, state: State) {
    FlowRow(
        horizontalArrangement = flow.rowGravity.arrangement,
        modifier = Modifier
            .testTag(TEST_TAG_FLOW)
            .fillMaxWidth()
    ) {
        flow.items.forEachIndexed { pos, item ->
            key(pos) {
                val isGone by remember(item, state) { item.isGoneFlow(state) }.collectAsState(item.isGone(state))
                if (!isGone) {
                    Column(
                        modifier = Modifier
                            .visibility(item, state)
                            .width(item.width)
                    ) {
                        RenderContent(item.content, state)
                    }
                }
            }
        }
    }
}
