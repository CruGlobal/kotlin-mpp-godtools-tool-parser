package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip

class RenderInlineTipPaparazziTest : BasePaparazziTest() {
    private val state = State().apply {
        showTips.value = true
    }

    override val tips = listOf(
        Tip(id = "ask", type = Tip.Type.ASK),
        Tip(id = "consider", type = Tip.Type.CONSIDER),
        Tip(id = "prepare", type = Tip.Type.PREPARE),
        Tip(id = "quote", type = Tip.Type.QUOTE),
        Tip(id = "tip", type = Tip.Type.TIP),
    )

    @Test
    fun `RenderInlineTip() - Completed`() {
        state.completedTips.value = setOf("ask", "consider", "prepare", "quote", "tip")
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
            ),
            state = state,
        )
    }
}
