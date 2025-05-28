package org.cru.godtools.shared.renderer.content

import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Link
import org.cru.godtools.shared.tool.parser.model.Text

class RenderLinkPaparazziTest : BasePaparazziTest(renderingMode = RenderingMode.SHRINK) {
    @Test
    fun `RenderLink() - Simple`() = contentSnapshot {
        RenderContentStack(listOf(Link(text = Text(text = "Simple Text"))))
    }
}
