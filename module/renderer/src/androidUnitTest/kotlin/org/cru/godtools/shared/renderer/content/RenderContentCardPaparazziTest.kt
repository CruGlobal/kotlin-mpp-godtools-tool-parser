package org.cru.godtools.shared.renderer.content

import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Card
import org.cru.godtools.shared.tool.parser.model.Text

class RenderContentCardPaparazziTest: BasePaparazziTest() {

    @Test
    fun `RenderContentCard() - Background Color`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Card(
                    backgroundColor = RGB("#34ebde"),
                    content = listOf(
                        Text(text = "Text 1"),
                        Text(text = "Text 2"),
                        Text(text = "Text 3")
                    )
                ),
                Card(backgroundColor = RGB("#ff23cc")),
                Card(backgroundColor = RGB("#c6eb34"))
            )
        )
    }
}
