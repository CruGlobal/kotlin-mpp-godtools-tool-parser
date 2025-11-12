package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Link
import org.cru.godtools.shared.tool.parser.model.Text

class RenderLinkPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderLink() - Simple`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Link(
                    text = { Text(it, "Link 1") }
                ),
                Link(
                    text = { Text(it, "Link 2") }
                ),
                Link(
                    text = { Text(it, "Link 3") }
                )
            )
        )
    }

    @Test
    fun `RenderLink() - TextAlign`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Link(
                    text = { Text(it, "Align Start", textAlign = Text.Align.START) }
                ),
                Link(
                    text = { Text(it, "Align Center", textAlign = Text.Align.CENTER) }
                ),
                Link(
                    text = { Text(it, "Align End", textAlign = Text.Align.END) }
                )
            )
        )
    }
}
