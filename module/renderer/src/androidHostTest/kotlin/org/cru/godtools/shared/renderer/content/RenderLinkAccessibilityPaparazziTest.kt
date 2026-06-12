package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Link
import org.cru.godtools.shared.tool.parser.model.Text

class RenderLinkAccessibilityPaparazziTest : BasePaparazziTest(accessibilityMode = AccessibilityMode.ACCESSIBILITY) {
    @Test
    fun `RenderLink() - TextAlign`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Link(
                    text = { Text(it, "Align Start", textAlign = Text.Align.START) }
                )
            )
        )
    }
}
