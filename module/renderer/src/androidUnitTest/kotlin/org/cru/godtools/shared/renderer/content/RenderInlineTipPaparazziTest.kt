package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip

class RenderInlineTipPaparazziTest : BasePaparazziTest() {
    private val state = State().apply {
        showTips.value = true
    }

    @Test
    fun `RenderInlineTip() - Completed`() {
        runBlocking {
            tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, "ask")
            tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, "consider")
            tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, "prepare")
            tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, "quote")
            tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, "tip")
        }
        allTipsSnapshot()
    }

    @Test
    fun `RenderInlineTip() - Not Completed`() = allTipsSnapshot()

    @Test
    fun `RenderInlineTip() - Tips Disabled`() {
        state.showTips.value = false
        allTipsSnapshot()
    }

    private fun allTipsSnapshot() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(text = "Ask Tip:"),
                InlineTip(manifest, "ask"),
                Text(text = "Consider Tip:"),
                InlineTip(manifest, "consider"),
                Text(text = "Prepare Tip:"),
                InlineTip(manifest, "prepare"),
                Text(text = "Quote Tip:"),
                InlineTip(manifest, "quote"),
                Text(text = "Tip:"),
                InlineTip(manifest, "tip"),
                Text(text = "Invalid/Missing Tip:"),
                InlineTip(manifest, "missing"),
            ),
            state = state,
        )
    }
}
