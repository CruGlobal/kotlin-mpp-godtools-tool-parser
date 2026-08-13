package org.cru.godtools.shared.renderer.tips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.tips.Tip

class RenderTipIconsPaparazziTest : BasePaparazziTest() {
    @Test
    fun `TipIcons() - All variants`() {
        val incompleteTips = Tip.Type.entries.map { type -> Tip(manifest, type = type) }
        val completeTips = Tip.Type.entries.map { type -> Tip(manifest, type = type) }
        runBlocking {
            completeTips.forEach { tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, it.id) }
        }

        contentSnapshot {
            val state = remember { State() }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TipIconsRow(incompleteTips) { TipIcon(it) }
                TipIconsRow(completeTips) { TipIcon(it) }
                TipIconsRow(incompleteTips) { TipUpArrow(it, state) }
                TipIconsRow(completeTips) { TipUpArrow(it, state) }
                TipIconsRow(incompleteTips) { TipDownArrow(it, state) }
                TipIconsRow(completeTips) { TipDownArrow(it, state) }
            }
        }
    }
}

@Composable
private fun TipIconsRow(tips: List<Tip>, icon: @Composable (Tip) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        tips.forEach { icon(it) }
    }
}
