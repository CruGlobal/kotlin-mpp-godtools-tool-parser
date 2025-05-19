package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.Text

class RenderParagraphPaparazziTest : BasePaparazziTest(renderingMode = RenderingMode.SHRINK) {
    @Test
    fun `RenderParagraph() - Defaults`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Paragraph(
                    content = { listOf(Text(it, "Default Paragraph")) },
                ),
            ),
            modifier = Modifier.border(1.dp, Color.Red)
        )
    }
}
