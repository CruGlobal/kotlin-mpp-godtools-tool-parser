package org.cru.godtools.shared.renderer.content

import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Text

class RenderTextPaparazziTest : BasePaparazziTest(renderingMode = RenderingMode.SHRINK) {
    @Test
    fun `RenderText() - Simple`() = contentSnapshot {
        RenderText(Text(text = "Simple Text"))
    }

    @Test
    fun `RenderText() - Styles`() = contentSnapshot {
        RenderText(Text(text = "Italic Text", textStyles = setOf(Text.Style.ITALIC)))
        RenderText(Text(text = "Underline Text", textStyles = setOf(Text.Style.UNDERLINE)))
        RenderText(Text(text = "Italic Underline Text", textStyles = setOf(Text.Style.ITALIC, Text.Style.UNDERLINE)))
    }

    @Test
    fun `RenderText() - Font Weight`() = contentSnapshot {
        RenderText(Text(text = "Font Weight 100", fontWeight = 100))
        RenderText(Text(text = "Font Weight 200", fontWeight = 200))
        RenderText(Text(text = "Font Weight 300", fontWeight = 300))
        RenderText(Text(text = "Font Weight 400", fontWeight = 400))
        RenderText(Text(text = "Font Weight 500", fontWeight = 500))
        RenderText(Text(text = "Font Weight 600", fontWeight = 600))
        RenderText(Text(text = "Font Weight 700", fontWeight = 700))
        RenderText(Text(text = "Font Weight 800", fontWeight = 800))
        RenderText(Text(text = "Font Weight 900", fontWeight = 900))
        RenderText(Text(text = "Font Weight 1000", fontWeight = 1000))
    }
}
